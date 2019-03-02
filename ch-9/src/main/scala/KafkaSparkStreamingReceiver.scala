import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

object KafkaSparkStreamingReceiver {

  def main(args: Array[String]) {
    Logger.getRootLogger.setLevel(Level.WARN)
    if (args.length < 1) {
      System.err.println("Usage: KafkaAndSparkStreaming")
      System.exit(1)
    }
    /*configure all the parameters for createStream*/
    val zkQuorum = "localhost:2181, localhost:2182"
    val group = "kafka-receiver-based"
    val numThreads = "1"
    /*createStream takes topic name and number of thread needed to pull data in parallel*/
    val topics = Set("pharma").map((_, numThreads.toInt)).toMap

    /*create spark streaming context*/
    val sparkConf = new SparkConf().setAppName("KafkaAndSparkStreamingReceiver").setMaster("local[*]")
    val ssc =  new StreamingContext(sparkConf, Seconds(10))
    /*perform checkpoiting*/
    ssc.checkpoint("hdfs://localhost:9000/chk-data/kafka/")

    /*create stream using KafkaUtils and createStream*/
    val rawStream = KafkaUtils.createStream(ssc, zkQuorum, group, topics)

    /*raw streams prints the data along with stream information*/
    rawStream.print()

    /*get only streams of data*/
    val lines = rawStream.map(_._2)

    /*perform analytics*/
    lines.foreachRDD{rdd =>

      /*create SQL context*/
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._
      /*convert rdd into dataframe by providing header information*/
      val pharmaAnalytics = rdd.toDF("country","time","pc_healthxp","pc_gdp","usd_cap","flag_codes","total_spend")
      /*create temporary table*/
      pharmaAnalyticsDF.registerTempTable("pharmaAnalytics")

      /*find total gdp spending per country*/
      val gdpByCountryDF = sqlContext.sql("select country, sum(total_gdp) as total_gdp from pharmaAnalytics group by country")
      gdpByCountryDF.show(10)

      /*find total capital spending in USD per country in year 2015*/
      val spendingPerCapitaDF = sqlContext.sql("select country, sum(usd_cap) as spending_per_capita from pharmaAnalytics group by country having time=2015")
      spendingPerCapitaDF.show(10)

    }

    /*start the streaming job*/
    ssc.start()
    ssc.awaitTermination()
  }
}
