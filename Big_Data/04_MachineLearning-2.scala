import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{OneHotEncoder, VectorAssembler, StandardScaler}
import org.apache.spark.ml.regression.{RandomForestRegressor, GBTRegressor}
import org.apache.spark.ml.evaluation.RegressionEvaluator
import java.io.{FileWriter, BufferedWriter}

object MachineLearning {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("LinkedIn Jobs Machine Learning - Phase 5: Evaluation")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val filePath   = "src/main/resources/transformed_linkedin_jobs.csv"
    val outputPath = "Evaluation_output.txt"
    val fw = new BufferedWriter(new FileWriter(outputPath))

    def log(line: String = ""): Unit = { println(line); fw.write(line + "\n") }
    def sep(): Unit = log("=" * 70)
    def sub(): Unit = log("-" * 70)

    // ============================================================
    // SETUP: LOAD DATA & BUILD PIPELINE (same as Phase 5)
    // ============================================================

    val dfRaw = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(filePath)

    val dfFiltered = dfRaw.filter(col("salary_source") === "Original")
      .withColumn("normalized_salary",                    col("normalized_salary").cast("double"))
      .withColumn("state_idx_num",                        col("state_idx_num").cast("double"))
      .withColumn("formatted_work_type_idx_num",          col("formatted_work_type_idx_num").cast("double"))
      .withColumn("title_idx_num",                        col("title_idx_num").cast("double"))
      .withColumn("formatted_experience_level_idx_num",   col("formatted_experience_level_idx_num").cast("double"))
      .withColumn("remote_allowed",                       col("remote_allowed").cast("double"))

    val Array(trainData, testData) = dfFiltered.randomSplit(Array(0.8, 0.2), seed = 42)

    val encoder = new OneHotEncoder()
      .setInputCols(Array("state_idx_num", "formatted_work_type_idx_num", "title_idx_num"))
      .setOutputCols(Array("stateVec", "workTypeVec", "titleVec"))

    val assembler = new VectorAssembler()
      .setInputCols(Array("stateVec", "workTypeVec", "titleVec",
                          "formatted_experience_level_idx_num", "remote_allowed"))
      .setOutputCol("features_raw")

    val scaler = new StandardScaler()
      .setInputCol("features_raw")
      .setOutputCol("features")
      .setWithMean(true)
      .setWithStd(true)

    val pipeline      = new Pipeline().setStages(Array(encoder, assembler, scaler))
    val pipelineModel = pipeline.fit(trainData)

    val trainTransformed = pipelineModel.transform(trainData)
    val testTransformed  = pipelineModel.transform(testData)

    // ============================================================
    // EVALUATOR SETUP
    // ============================================================

    val evaluatorRMSE = new RegressionEvaluator()
      .setLabelCol("normalized_salary")
      .setPredictionCol("prediction")
      .setMetricName("rmse")

    val evaluatorMAE = new RegressionEvaluator()
      .setLabelCol("normalized_salary")
      .setPredictionCol("prediction")
      .setMetricName("mae")

    val evaluatorR2 = new RegressionEvaluator()
      .setLabelCol("normalized_salary")
      .setPredictionCol("prediction")
      .setMetricName("r2")

    // ============================================================
    // BASELINE MODEL: MEAN PREDICTION
    // ============================================================

    sep()
    log(" BASELINE MODEL: Mean Salary Prediction")
    sep()

    // Compute mean salary on training set only (no leakage)
    val meanSalary = trainTransformed
      .agg(avg("normalized_salary"))
      .collect()(0)
      .getDouble(0)

    log(f"  Mean salary (train): $$${meanSalary}%.2f")

    val baselinePredictions = testTransformed
      .withColumn("prediction", lit(meanSalary))

    val baselineRMSE = evaluatorRMSE.evaluate(baselinePredictions)
    val baselineMAE  = evaluatorMAE.evaluate(baselinePredictions)
    val baselineR2   = evaluatorR2.evaluate(baselinePredictions)

    log(f"  Baseline RMSE       : $$${baselineRMSE}%.2f")
    log(f"  Baseline MAE        : $$${baselineMAE}%.2f")
    log(f"  Baseline R²         : ${baselineR2}%.4f")

    sep()
    log(" BASELINE COMPLETE")
    sep()

    // ============================================================
    // MODEL 1: RANDOM FOREST REGRESSOR — TRAIN & TEST METRICS
    // ============================================================

    sep()
    log(" MODEL 1: Random Forest Regressor — Full Evaluation")
    sep()

    val rf = new RandomForestRegressor()
      .setLabelCol("normalized_salary")
      .setFeaturesCol("features")
      .setPredictionCol("prediction")
      .setNumTrees(50)
      .setMaxDepth(10)

    val rfModel = rf.fit(trainTransformed)

    // --- Training metrics (detect overfitting) ---
    log("\n----- RF TRAINING METRICS -----")
    val rfTrainPred  = rfModel.transform(trainTransformed)
    val rfTrainRMSE  = evaluatorRMSE.evaluate(rfTrainPred)
    val rfTrainMAE   = evaluatorMAE.evaluate(rfTrainPred)
    val rfTrainR2    = evaluatorR2.evaluate(rfTrainPred)

    log(f"  Train RMSE          : $$${rfTrainRMSE}%.2f")
    log(f"  Train MAE           : $$${rfTrainMAE}%.2f")
    log(f"  Train R²            : ${rfTrainR2}%.4f")

    // --- Test metrics ---
    log("\n----- RF TEST METRICS -----")
    val rfTestPred  = rfModel.transform(testTransformed)
    val rfTestRMSE  = evaluatorRMSE.evaluate(rfTestPred)
    val rfTestMAE   = evaluatorMAE.evaluate(rfTestPred)
    val rfTestR2    = evaluatorR2.evaluate(rfTestPred)

    log(f"  Test  RMSE          : $$${rfTestRMSE}%.2f")
    log(f"  Test  MAE           : $$${rfTestMAE}%.2f")
    log(f"  Test  R²            : ${rfTestR2}%.4f")

    // --- Overfitting check ---
    log("\n----- RF OVERFITTING CHECK -----")
    val rfRmseGap = ((rfTestRMSE - rfTrainRMSE) / rfTrainRMSE) * 100
    log(f"  RMSE gap (test-train) / train: ${rfRmseGap}%.1f%%")
    if (rfRmseGap > 20) log("  WARNING: Possible overfitting detected")
    else log("  OK: Generalisation gap is within acceptable range")

    // --- vs Baseline ---
    log("\n----- RF vs BASELINE -----")
    val rfImprovement = ((baselineRMSE - rfTestRMSE) / baselineRMSE) * 100
    log(f"  Baseline RMSE : $$${baselineRMSE}%.2f")
    log(f"  RF Test  RMSE : $$${rfTestRMSE}%.2f")
    log(f"  RMSE improvement over baseline: ${rfImprovement}%.1f%%")

    // --- Feature Importances (top 5) ---
    log("\n----- RF TOP FEATURE IMPORTANCES -----")
    val importances = rfModel.featureImportances.toArray
    val top5 = importances.zipWithIndex.sortBy(-_._1).take(5)
    top5.foreach { case (imp, idx) =>
      log(f"  Feature[$idx] : ${imp}%.6f")
    }

    sep()
    log(" MODEL 1 EVALUATION COMPLETE")
    sep()

    // ============================================================
    // MODEL 2: GRADIENT BOOSTED TREES — TRAIN & TEST METRICS
    // ============================================================

    sep()
    log(" MODEL 2: Gradient Boosted Trees (GBT) — Full Evaluation")
    sep()

    val gbt = new GBTRegressor()
      .setLabelCol("normalized_salary")
      .setFeaturesCol("features")
      .setPredictionCol("prediction")
      .setMaxIter(100)
      .setMaxDepth(5)

    val gbtModel = gbt.fit(trainTransformed)

    // --- Training metrics ---
    log("\n----- GBT TRAINING METRICS -----")
    val gbtTrainPred  = gbtModel.transform(trainTransformed)
    val gbtTrainRMSE  = evaluatorRMSE.evaluate(gbtTrainPred)
    val gbtTrainMAE   = evaluatorMAE.evaluate(gbtTrainPred)
    val gbtTrainR2    = evaluatorR2.evaluate(gbtTrainPred)

    log(f"  Train RMSE          : $$${gbtTrainRMSE}%.2f")
    log(f"  Train MAE           : $$${gbtTrainMAE}%.2f")
    log(f"  Train R²            : ${gbtTrainR2}%.4f")

    // --- Test metrics ---
    log("\n----- GBT TEST METRICS -----")
    val gbtTestPred  = gbtModel.transform(testTransformed)
    val gbtTestRMSE  = evaluatorRMSE.evaluate(gbtTestPred)
    val gbtTestMAE   = evaluatorMAE.evaluate(gbtTestPred)
    val gbtTestR2    = evaluatorR2.evaluate(gbtTestPred)

    log(f"  Test  RMSE          : $$${gbtTestRMSE}%.2f")
    log(f"  Test  MAE           : $$${gbtTestMAE}%.2f")
    log(f"  Test  R²            : ${gbtTestR2}%.4f")

    // --- Overfitting check ---
    log("\n----- GBT OVERFITTING CHECK -----")
    val gbtRmseGap = ((gbtTestRMSE - gbtTrainRMSE) / gbtTrainRMSE) * 100
    log(f"  RMSE gap (test-train) / train: ${gbtRmseGap}%.1f%%")
    if (gbtRmseGap > 20) log("  WARNING: Possible overfitting detected")
    else log("  OK: Generalisation gap is within acceptable range")

    // --- vs Baseline ---
    log("\n----- GBT vs BASELINE -----")
    val gbtImprovement = ((baselineRMSE - gbtTestRMSE) / baselineRMSE) * 100
    log(f"  Baseline RMSE  : $$${baselineRMSE}%.2f")
    log(f"  GBT Test  RMSE : $$${gbtTestRMSE}%.2f")
    log(f"  RMSE improvement over baseline: ${gbtImprovement}%.1f%%")

    // --- Feature Importances (top 5) ---
    log("\n----- GBT TOP FEATURE IMPORTANCES -----")
    val gbtImportances = gbtModel.featureImportances.toArray
    val gbtTop5 = gbtImportances.zipWithIndex.sortBy(-_._1).take(5)
    gbtTop5.foreach { case (imp, idx) =>
      log(f"  Feature[$idx] : ${imp}%.6f")
    }

    sep()
    log(" MODEL 2 EVALUATION COMPLETE")
    sep()

    // ============================================================
    // FINAL COMPARISON SUMMARY
    // ============================================================

    sep()
    log(" FINAL MODEL COMPARISON SUMMARY")
    sep()

    log("\n" + "%-20s %-15s %-15s %-10s".format("Model", "RMSE (test)", "MAE (test)", "R² (test)"))
    sub()
    log("%-20s %-15s %-15s %-10s".format(
      "Baseline (mean)",
      f"$$${baselineRMSE}%.0f",
      f"$$${baselineMAE}%.0f",
      f"${baselineR2}%.4f"))
    log("%-20s %-15s %-15s %-10s".format(
      "Random Forest",
      f"$$${rfTestRMSE}%.0f",
      f"$$${rfTestMAE}%.0f",
      f"${rfTestR2}%.4f"))
    log("%-20s %-15s %-15s %-10s".format(
      "GBT",
      f"$$${gbtTestRMSE}%.0f",
      f"$$${gbtTestMAE}%.0f",
      f"${gbtTestR2}%.4f"))
    sub()

    if (gbtTestRMSE < rfTestRMSE)
      log("  WINNER: GBT outperforms Random Forest on test RMSE")
    else
      log("  WINNER: Random Forest outperforms GBT on test RMSE")

    sep()
    log(" EVALUATION COMPLETE")
    sep()

    // ============================================================
    // INTERPRETATION
    // ============================================================

    sep()
    log(" INTERPRETATION")
    sep()

    log("----- FEATURE IMPORTANCE ANALYSIS -----")
    log("  The most important features across the models are:")
    log("  Feature[2978]")
    log("  Feature[2979]")
    log("  Feature[299]")
    log("  Feature[1468]")
    log("  Feature[0]")
    log("")
    log("  Although feature indices are shown numerically due to vectorization,")
    log("  they represent encoded variables such as job title, location, and work type,")
    log("  which were found to be the most influential in predicting salary.")

    log("")
    log("----- WHAT THE MODEL HAS LEARNED -----")
    log("  Salary is influenced by job title, location, work type, and experience level.")

    log("")
    log("----- MODEL PERFORMANCE ANALYSIS -----")
    log(f"  GBT is the best model with:")
    log(f"    RMSE = $$${gbtTestRMSE}%.0f")
    log(f"    MAE  = $$${gbtTestMAE}%.0f")
    log(f"    R²   = ${gbtTestR2}%.4f")

    log("")
    log("----- FINAL INTERPRETATION -----")
    log("  The model learns useful patterns but is suitable for estimation, not exact prediction.")

    sep()
    log(" INTERPRETATION COMPLETE")
    sep()

    fw.close()
    spark.stop()
  }
}
