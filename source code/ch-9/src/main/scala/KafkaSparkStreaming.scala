package ch-9

import java.util.Properties

import kafka.producer._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming._
import _root_.kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.Logging
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.kafka.KafkaUtils

object KafkaAndSparkStreaming {

    /*main method*/
    def main(args: Array[String])
    {
        /*logging at warning level*/
        Logger.getRootLogger.setLevel(Level.WARN)

        /*Since spark streaming is a consumer, we need to pass it topics*/
        val topics = Set("pharma")
        /*pass kafka configuration details*/
        val kafkaParameters = Map[String, String](
        "metadata.broker.list" -> "localhost:9092",
        ConsumerConfig.AutoOffsetRest -> "earliest",
        "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
        ConsumerConfig.AutoOffsetRest -> "kafka",
        "group.id" -> "consumerGroup",
        ConsumerConfig.AutoCommit.toString -> "false"
        )

        /*create spark configurations*/
        val sparkConf = new SparkConf().setAppName("KafkaAndSparkStreaming").setMaster("local[*]")
        /*create spark streaming context*/
        val ssc =  new StreamingContext(sparkConf, Seconds(30))
        /*There are multiple ways to get data from kafka topic using KafkaUtils class.
         * One such method to get data using createDirectStream where this method pulls data using direct stream approach*/
        val rawStream = KafkaUtils.createDirectStream[String, String, StringDecoder,StringDecoder](ssc, kafkaParameters, topics)

        /*gets raw data from topic, explode it, and break it into words */
        val records= rawStream.map(_._2)

        /*Get metadata of the records and perform analytics on records*/
        rawStream.foreachRDD{rdd =>
        /*get offset ranges of partitions that will be used to get partition and offset information
         * and also this information will be used to commit the offset*/
        val rangeOfOffsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        /*get partition information*/
        rdd.foreachPartition { iter =>
        val metadata = rangeOfOffsets(TaskContext.get.partitionId)
        /*print topic, partition, fromoofset and lastoffset of each partition*/
        println(s"${metadata.topic} ${metadata.partition} ${metadata.fromOffset} ${metadata.untilOffset}")
        }
        /*using SQL on top of streams*/
        /*create SQL context*/
        val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
        /*convert rdd into dataframe by providing header information*/
        val pharmaAnalytics = rdd.toDF("country","time","pc_healthxp","pc_gdp","usd_cap","flag_codes","total_spend")
        /*create temporary table*/
        pharmaAnalyticsDF.registerTempTable("pharmaAnalytics")

        /*find total gdp spending per country*/
        val gdpByCountryDF = sqlContext.sql("select country, sum(total_gdp) as total_gdp from pharmaAnalytics group by country")
        gdpByCountryDF.show(10)        

        /*commit the offset after all the processing is completed*/
        stream.asInstanceOf[CanCommitOffsets].commitAsync(rangeOfOffsets)
    }
}