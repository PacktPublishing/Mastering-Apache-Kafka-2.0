package spark.basic.cl31

package com.spark.tutorial

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
  def main(args: Array[String]) {
    /*logging at warning level*/
    Logger.getRootLogger.setLevel(Level.WARN)

    /*Since spark streaming is a consumer, we need to pass it topics*/
    val topics = Set("testTopic")
    /*pass kafka configuration details*/
    val kafkaParams = Map[String, String](
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
    /*In streaming it is recommended to enable checkpoint*/
    ssc.checkpoint("checkpoint")
    /*There are multiple ways to get data from kafka topic using KafkaUtils class.
    * One such method to get data using createDirectStream where this method pulls data using direct stream approach*/
    val rawStream = KafkaUtils.createDirectStream[String, String, StringDecoder,StringDecoder](ssc, kafkaParams, topics)

    /*gets raw data from topic, explode it, and break it into words */
    val words = rawStream.map(_._2).flatMap(x => x.split(" "))
    /*count those words*/
    val wc = words.map(x => (x,1)).reduceByKey(_+_)

    /*Store raw data into HDFS*/
    rawStream.foreachRDD{rdd =>
      rdd.map(_._2).saveAsTextFile("hdfs://localhost:9000/data/kafka/testTopic/"
      + System.currentTimeMillis().toString)

      /*get offset ranges of partitions that will be used to get partition and offset information
      * and also this information will be used to commit the offset*/
      val rangeOfOffsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      /*get partition information*/
      rdd.foreachPartition { iter =>
        val metadata = rangeOfOffsets(TaskContext.get.partitionId)
        /*print topic, partition, fromoofset and lastoffset of each partition*/
        println(s"${metadata.topic} ${metadata.partition} ${metadata.fromOffset} ${metadata.untilOffset}")
      }
      /*using SQL on top of strams*/
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._
      /*converted the rdd into dataframe*/
      val wordsDF = rdd.toDF("dummy","eachword")
      /*create a temporary table to query*/
      wordsDF.registerTempTable("words")

      /*query the table*/
      val wcDF = sqlContext.sql("select eachword, count(*) as total from words group by eachword")
      /*show the results*/
      wcDF.show()

      /*commit the offset after all the processing is completed*/
      stream.asInstanceOf[CanCommitOffsets].commitAsync(rangeOfOffsets)

    }
    /*start the streaming and run infinitely until it is terminated*/
    ssc.start()
    ssc.awaitTermination()
  }
}