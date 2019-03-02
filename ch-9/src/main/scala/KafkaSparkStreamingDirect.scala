import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming._

object KafkaSparkStreamingDirect {

  def main(args: Array[String]) {
    Logger.getRootLogger.setLevel(Level.WARN)
    if (args.length < 1) {
      System.err.println("Usage: KafkaAndSparkStreaming using Direct approach")
      System.exit(1)
    }
    /*configure all the parameters for createStream*/
    val kafkaParameters = Map[String, String]
                      ("metadata.broker.list" -> "localhost:9092, another_localhost:9092",
                       "group.id" -> "kafka-direct-based",
                       "enable.auto.commit" -> "false",
                       "auto.offset.reset" -> "latest",
                        "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
                        "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
                      )
    /*createStream takes topic name and number of thread needed to pull data in parallel*/
    val topics = Set("pharma")

    /*create spark streaming context*/
    val sparkConf = new SparkConf().setAppName("KafkaAndSparkStreamingDirect").setMaster("local[*]")
    val ssc =  new StreamingContext(sparkConf, Seconds(10))

    /*create stream using KafkaUtils and createDirectStream*/
    val rawStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParameters, topicsSet)

    /*raw streams prints the data along with stream information*/
    rawStream.print()

    /*get only streams of data*/
    val lines = rawStream.map(_._2)

    /*perform analytics*/
    lines.foreachRDD{rdd =>

      /*create SQL context*/
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
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
