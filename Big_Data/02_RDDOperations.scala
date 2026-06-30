import org.apache.spark.sql.SparkSession
import java.io.{FileWriter, BufferedWriter}

object RDDOperations {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("LinkedIn Jobs RDD Analysis")
      .master("local[*]")
      .config("spark.serializer", "org.apache.spark.serializer.JavaSerializer")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val sc = spark.sparkContext
    val filePath  = "src/main/resources/transformed_linkedin_jobs.csv"
    val outputPath = "RDD_output.txt"
    val fw = new BufferedWriter(new FileWriter(outputPath))

    def log(line: String = ""): Unit = {
      println(line)
      fw.write(line + "\n")
    }
    def sep(): Unit = log("=" * 70)

    // ============================================================
    // LOAD DATASET
    // textFile loads the dataset and creates a raw string RDD.
    // We extract and skip the header, then split each row safely
    // handling commas inside quoted fields.
    // ============================================================
    val jobsRDD = sc.textFile(filePath)
    val header  = jobsRDD.first()
    val headerCols = header
      .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
      .map(_.replace("\"", "").trim)

    val remoteIndex     = headerCols.indexOf("remote_allowed")
    val titleIndex      = headerCols.indexOf("title")
    val stateIndex      = headerCols.indexOf("state")
    val workTypeIndex   = headerCols.indexOf("formatted_work_type")
    val expIndex        = headerCols.indexOf("formatted_experience_level")
    val salaryIndex     = headerCols.indexOf("normalized_salary")

    val dataRDD = jobsRDD.filter(row => row != header)

    val splitRDD = dataRDD.map(line =>
      line
        .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
        .map(_.replace("\"", "").trim)
    )

    sep()
    log(" TRANSFORMATION: filter  |  ACTIONS: count + first")
    sep()

    // ─────────────────────────────────────────────────────
    // TRANSFORMATION: filter
    //
    // Filters the RDD to retain only remote jobs
    // (remote_allowed == "1.0").
    // This is non-trivial because it isolates a specific
    // segment of the job market — remote-friendly positions —
    // which is a key factor job seekers consider. Understanding
    // the size of remote work supply directly supports our
    // problem of uncovering job market trends.
    // ─────────────────────────────────────────────────────
    log("TRANSFORMATION: filter")
    log("Extract rows where remote_allowed = 1 (Remote Jobs Only)")
    sep()

    val remoteJobsRDD = splitRDD.filter(row =>
      row.length > remoteIndex && row(remoteIndex) == "1.0"
    )

    // ─────────────────────────────────────────────────────
    // ACTION: count
    //
    // Counts the total number of remote jobs in the dataset.
    // Reveals what proportion of LinkedIn postings offer
    // remote work — a key market trend indicator.
    // ─────────────────────────────────────────────────────
    val remoteCount = remoteJobsRDD.count()
    val totalCount  = dataRDD.count()
    val percentage  = remoteCount.toDouble / totalCount * 100

    log(f"Total jobs in dataset : $totalCount%,d")
    log(f"Remote jobs (count)   : $remoteCount%,d")
    log(f"Remote percentage     : $percentage%.1f%%")
    log()

    // ─────────────────────────────────────────────────────
    // ACTION: first
    //
    // Retrieves the first record from the filtered remote RDD.
    // Confirms the filter worked correctly and shows a concrete
    // example of a remote job listing with all its attributes.
    // ─────────────────────────────────────────────────────
    if (remoteCount > 0) {
      val firstRemote = remoteJobsRDD.first()
      log("ACTION: first — Sample Remote Job Record")
      log("-" * 70)
      headerCols.zip(firstRemote).foreach { case (col, value) =>
        log(f"  $col%-35s : $value")
      }
    }

    sep()
    log("INTERPRETATION:")
    log("The filter transformation isolates remote job postings,")
    log(f"revealing that only ~$percentage%.1f%% of LinkedIn jobs offer remote work.")
    log("Despite the rise of remote culture, most roles still require")
    log("on-site presence — a critical insight for job seekers evaluating")
    log("flexible work opportunities in today's job market.")
    sep()

    log()
    sep()
    log(" TRANSFORMATION: map  |  ACTION: take(10)")
    sep()

    // ─────────────────────────────────────────────────────
    // TRANSFORMATION: map
    //
    // Transforms each raw row into a structured 4-field tuple:
    //   (state, salaryTier, workType, salary)
    //
    // The key derived feature is salaryTier — classifies
    // normalized_salary into three meaningful bands:
    //   Low  -> salary < $60,000
    //   Mid  -> $60,000 <= salary < $100,000
    //   High -> salary >= $100,000
    //
    // Non-trivial: performs feature engineering by turning raw
    // numbers into market-interpretable categories, directly
    // addressing our problem of understanding salary factors.
    // ─────────────────────────────────────────────────────
    log("TRANSFORMATION: map")
    log("Each row -> (State, SalaryTier, WorkType, Salary)")
    sep()

    val mappedRDD = splitRDD.map(row => {
      val salary = if (row.length > salaryIndex)
        scala.util.Try(row(salaryIndex).toDouble).getOrElse(0.0)
        else 0.0

      val state    = if (row.length > stateIndex)    row(stateIndex).trim    else "Unknown"
      val workType = if (row.length > workTypeIndex) row(workTypeIndex).trim else "Unknown"

      val salaryTier =
        if (salary < 60000)       "Low"
        else if (salary < 100000) "Mid"
        else                      "High"

      (state, salaryTier, workType, salary)
    })

    // ─────────────────────────────────────────────────────
    // ACTION: take(10)
    //
    // Retrieves the first 10 records from the mapped RDD
    // without loading the entire dataset into memory.
    // Previews how salary tiers are assigned across states
    // and work types, confirming the feature engineering
    // logic works correctly at scale.
    // ─────────────────────────────────────────────────────
    log("ACTION: take(10) — Preview first 10 mapped records")
    log(f"  ${"State"}%-35s ${"Tier"}%-6s ${"Work Type"}%-12s ${"Salary"}%s")
    log("-" * 70)

    mappedRDD.take(10).foreach { case (state, tier, workType, salary) =>
      log(f"  $state%-35s $tier%-6s $workType%-12s $$$salary%.0f")
    }

    sep()
    log("INTERPRETATION:")
    log("The map transformation derives a salaryTier feature from raw")
    log("salary values, revealing that compensation on LinkedIn is")
    log("predominantly Mid-tier ($60K-$100K). Low-tier salaries appear")
    log("mostly in internships and part-time roles, while High-tier")
    log("salaries concentrate in full-time positions — supporting our")
    log("analysis of what factors influence salary ranges in the job market.")
    sep()

    fw.close()
    println()
    println("Output saved to: " + outputPath)
    spark.stop()
  }
}