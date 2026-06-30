import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import java.io.{BufferedWriter, FileWriter}

object SQLOperations {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("LinkedIn Jobs SQL Operations")
      .master("local[*]")
      .config("spark.driver.extraJavaOptions",
        "--add-opens=java.base/java.nio=ALL-UNNAMED " +
        "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED " +
        "--add-opens=java.base/java.lang=ALL-UNNAMED " +
        "--add-opens=java.base/java.util=ALL-UNNAMED " +
        "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED " +
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED " +
        "--add-opens=java.base/java.io=ALL-UNNAMED " +
        "--add-opens=java.base/java.net=ALL-UNNAMED " +
        "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED " +
        "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED")
      .config("spark.executor.extraJavaOptions",
        "--add-opens=java.base/java.nio=ALL-UNNAMED " +
        "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED " +
        "--add-opens=java.base/java.lang=ALL-UNNAMED " +
        "--add-opens=java.base/java.util=ALL-UNNAMED " +
        "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED " +
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED " +
        "--add-opens=java.base/java.io=ALL-UNNAMED " +
        "--add-opens=java.base/java.net=ALL-UNNAMED " +
        "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED " +
        "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val filePath = if (args.length > 0) args(0) else "src/main/resources/transformed_linkedin_jobs.csv"
    val outputPath = if (args.length > 1) args(1) else "SQL_output.txt"
    val fw = new BufferedWriter(new FileWriter(outputPath))

    def log(line: String = ""): Unit = {
      println(line)
      fw.write(line + "\n")
    }

    def sep(): Unit = log("=" * 70)

    try {
      // ============================================================
      // LOAD DATASET & REGISTER TEMPORARY VIEW
      // ============================================================
      log("Loading dataset and registering temporary view...")

      val df = spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .option("multiLine", "true")
        .option("quote", "\"")
        .option("escape", "\"")
        .csv(filePath)

      val requiredColumns = Seq(
        "title",
        "formatted_work_type",
        "formatted_experience_level",
        "normalized_salary",
        "state"
      )

      val missingColumns = requiredColumns.filterNot(df.columns.contains)
      require(missingColumns.isEmpty,
        s"Missing required columns: ${missingColumns.mkString(", ")}")

      df.createOrReplaceTempView("jobs")

      log(s"Dataset loaded: ${df.count()} rows, ${df.columns.length} columns")
      log("Temporary view registered as: jobs")
      log(s"Columns: ${df.columns.mkString(", ")}")
      sep()

      // ============================================================
      // QUERY 1
      // Question: What is the average normalized salary per experience
      // level, considering only experience levels with more than 100
      // job postings?
      // SQL Features: GROUP BY + HAVING
      // ============================================================
      log()
      sep()
      log(" QUERY 1: Average Salary per Experience Level (GROUP BY + HAVING)")
      sep()
      log("Question: What is the average normalized salary per experience")
      log("level, considering only experience levels with more than 100 job postings?")
      log()

      val query1 = spark.sql(
        """
          |SELECT
          |  formatted_experience_level AS experience_level,
          |  ROUND(AVG(normalized_salary), 2) AS avg_salary,
          |  COUNT(*) AS job_count
          |FROM jobs
          |WHERE formatted_experience_level IS NOT NULL
          |  AND normalized_salary IS NOT NULL
          |GROUP BY formatted_experience_level
          |HAVING COUNT(*) > 100
          |ORDER BY avg_salary DESC
          |""".stripMargin)

      log(f"  ${"Experience Level"}%-25s ${"Avg Salary"}%12s ${"Job Count"}%10s")
      log("-" * 55)
      query1.collect().foreach { row =>
        val expLevel = Option(row.get(0)).map(_.toString).getOrElse("Unknown")
        val avgSalary = Option(row.get(1)).map(_.toString).getOrElse("N/A")
        val jobCount = row.getLong(2)
        log(f"  $expLevel%-25s $avgSalary%12s $jobCount%10d")
      }

      sep()

      // ============================================================
      // QUERY 2
      // Question: What are the top 10 states with the highest average
      // salary, limited to states with more than 50 job postings?
      // SQL Features: WHERE + GROUP BY + HAVING + ORDER BY + LIMIT
      // ============================================================
      log()
      sep()
      log(" QUERY 2: Top 10 States by Average Salary (WHERE + ORDER BY + LIMIT)")
      sep()
      log("Question: What are the top 10 states with the highest average")
      log("salary, limited to states with more than 50 job postings?")
      log()

      val query2 = spark.sql(
        """
          |SELECT
          |  state,
          |  ROUND(AVG(normalized_salary), 2) AS avg_salary,
          |  COUNT(*) AS job_count
          |FROM jobs
          |WHERE state IS NOT NULL
          |  AND TRIM(state) <> ''
          |  AND normalized_salary IS NOT NULL
          |GROUP BY state
          |HAVING COUNT(*) > 50
          |ORDER BY avg_salary DESC
          |LIMIT 10
          |""".stripMargin)

      log(f"  ${"State"}%-35s ${"Avg Salary"}%12s ${"Job Count"}%10s")
      log("-" * 62)
      query2.collect().foreach { row =>
        val state = Option(row.get(0)).map(_.toString).getOrElse("Unknown")
        val avgSalary = Option(row.get(1)).map(_.toString).getOrElse("N/A")
        val jobCount = row.getLong(2)
        log(f"  $state%-35s $avgSalary%12s $jobCount%10d")
      }

      sep()

      // ============================================================
      // QUERY 3
      // Question: How does each job's salary rank within its experience level?
      // SQL Features: Window Function + RANK
      // ============================================================

      log()
      sep()
      log(" QUERY 3: Salary Rank within Each Experience Level (WINDOW + RANK)")
      sep()
      log("Question: How does each job's salary rank within its experience level?")
      log()

      val query3 = spark.sql(
        """
        |SELECT
        |  title,
        |  formatted_experience_level AS experience_level,
        |  normalized_salary,
        |  RANK() OVER (
        |     PARTITION BY formatted_experience_level
        |     ORDER BY normalized_salary DESC
        |  ) AS salary_rank
        |FROM jobs
        |WHERE normalized_salary IS NOT NULL
        |  AND formatted_experience_level IS NOT NULL
        |ORDER BY experience_level, salary_rank
        |LIMIT 50
        |""".stripMargin)

      log(f"  ${"Title"}%-45s ${"Experience Level"}%-20s ${"Salary"}%12s ${"Rank"}%6s")
      log("-" * 90)

      query3.collect().foreach { row =>

        val title = Option(row.getAs[String]("title"))
          .map(_.replace("\"",""))
          .map(_.take(43))
          .getOrElse("Unknown")

        val exp = Option(row.getAs[String]("experience_level"))
          .getOrElse("Unknown")

        val salary = Option(row.getAs[Any]("normalized_salary")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v: Float)  => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        val rank = row.getAs[Int]("salary_rank")

        log(f"  $title%-45s $exp%-20s $salary%12s $rank%6d")
      }

      sep()

      // ============================================================
      // QUERY 4
      // Question: Which jobs have a salary above their state's average salary?
      // SQL Features: CTE + Subquery + JOIN
      // ============================================================
      log()
      sep()
      log(" QUERY 4: Jobs Above State Average Salary (CTE + Subquery + JOIN)")
      sep()
      log("Question: Which jobs have a salary above their state's average salary?")
      log()

      val query4 = spark.sql(
        """
        |WITH state_avg AS (
        |  SELECT
        |    state,
        |    ROUND(AVG(normalized_salary), 2) AS avg_state_salary
        |  FROM jobs
        |  WHERE state IS NOT NULL
        |    AND TRIM(state) <> ''
        |    AND normalized_salary IS NOT NULL
        |  GROUP BY state
        |)
        |SELECT
        |  j.title,
        |  j.state,
        |  ROUND(j.normalized_salary, 2)    AS salary,
        |  sa.avg_state_salary               AS state_avg_salary,
        |  ROUND(j.normalized_salary - sa.avg_state_salary, 2) AS above_avg_by
        |FROM jobs j
        |JOIN state_avg sa ON j.state = sa.state
        |WHERE j.normalized_salary > sa.avg_state_salary
        |  AND j.state IS NOT NULL
        |ORDER BY above_avg_by DESC
        |LIMIT 20
        |""".stripMargin)

      log(f"  ${"Title"}%-40s ${"State"}%-15s ${"Salary"}%12s ${"State Avg"}%12s ${"Above By"}%12s")
      log("-" * 97)
      query4.collect().foreach { row =>
        val title = Option(row.getAs[String]("title"))
          .map(_.replace("\"", ""))
          .map(_.take(38))
          .getOrElse("Unknown")
        val state = Option(row.getAs[String]("state"))
          .map(_.take(13))
          .getOrElse("Unknown")
        val salary = Option(row.getAs[Any]("salary")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        val stateAvg = Option(row.getAs[Any]("state_avg_salary")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        val aboveBy = Option(row.getAs[Any]("above_avg_by")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        log(f"  $title%-40s $state%-15s $salary%12s $stateAvg%12s $aboveBy%12s")
      }

      sep()

      // ============================================================
      // QUERY 5
      // Question: What is the salary distribution across work types,
      // including average, variance, percentiles, and distinct job title count?
      // SQL Features: Statistical Summary (AVG, VAR_POP, PERCENTILE_APPROX,
      //               COUNT DISTINCT, STDDEV_POP)
      // ============================================================
      log()
      sep()
      log(" QUERY 5: Salary Distribution by Work Type (Statistical Summary)")
      sep()
      log("Question: What is the salary distribution across work types,")
      log("including average, variance, percentiles, and distinct job title count?")
      log()

      val query5 = spark.sql(
        """
        |SELECT
        |  formatted_work_type                                       AS work_type,
        |  COUNT(*)                                                  AS total_jobs,
        |  COUNT(DISTINCT title)                                     AS distinct_titles,
        |  ROUND(AVG(normalized_salary), 2)                         AS avg_salary,
        |  ROUND(STDDEV_POP(normalized_salary), 2)                  AS salary_stddev,
        |  ROUND(VAR_POP(normalized_salary), 2)                     AS salary_variance,
        |  ROUND(PERCENTILE_APPROX(normalized_salary, 0.25), 2)     AS p25_salary,
        |  ROUND(PERCENTILE_APPROX(normalized_salary, 0.50), 2)     AS median_salary,
        |  ROUND(PERCENTILE_APPROX(normalized_salary, 0.75), 2)     AS p75_salary
        |FROM jobs
        |WHERE formatted_work_type IS NOT NULL
        |  AND normalized_salary IS NOT NULL
        |GROUP BY formatted_work_type
        |ORDER BY avg_salary DESC
        |""".stripMargin)

      log(f"  ${"Work Type"}%-15s ${"Jobs"}%8s ${"Titles"}%8s ${"Avg Salary"}%12s ${"Std Dev"}%12s ${"P25"}%10s ${"Median"}%10s ${"P75"}%10s")
      log("-" * 100)
      query5.collect().foreach { row =>
        val workType       = Option(row.getAs[String]("work_type")).getOrElse("Unknown")
        val totalJobs      = row.getLong(1)
        val distinctTitles = row.getLong(2)
        val avgSalary = Option(row.getAs[Any]("avg_salary")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        val stddev = Option(row.getAs[Any]("salary_stddev")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        val p25 = Option(row.getAs[Any]("p25_salary")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        val median = Option(row.getAs[Any]("median_salary")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        val p75 = Option(row.getAs[Any]("p75_salary")) match {
          case Some(v: Double) => f"$v%.2f"
          case Some(v)         => v.toString
          case None            => "N/A"
        }
        log(f"  $workType%-15s $totalJobs%8d $distinctTitles%8d $avgSalary%12s $stddev%12s $p25%10s $median%10s $p75%10s")
      }

      sep()
      log()
      log("Output saved to: " + outputPath)

    } finally {
      fw.close()
      spark.stop()
    }
  }
}