package ch-3

import java.lang.Long
import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.{KStream, KTable, Materialized}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import org.apache.kafka.common.serialization.Serdes
import Serdes._

import scala.language.implicitConversions

/*case class to apply schema to the fields coming in input*/
case class PharmaClass(country: String,
                       year: String,
                       pcnt_health_expend: String,
                       pcnt_gdp: String,
                       usd_capital_expend: String,
                       flag_codes: String,
                       total_expend: String)

object KafkaStreams {
  def main(args: Array[String]): Unit = {

    /*configurations for kafka streams*/
    val config: Properties = {
      val p = new Properties()
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
      p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
      p
    }

    /*serde for PharmaClass case class so that everytime there is no need to serialize/deserialize the fields*/
    implicit val pharmaClassSerde: Serde[PharmaClass] = new AvroSerde

    /*create the topology.*/
    val streamBuilder: StreamsBuilder = new StreamsBuilder

    /*create streams using KStream class, natively supported by DSL*/
    val rawData: KStream[String, String] = streamBuilder.stream[String, String]("pharma")
    /*break the data on ',' and return it to apply the schema on the fields*/
    val eachLinesAsArray : KStream[String, Array[String]]  = rawData
      .mapValues(eachLine => eachLine.split(","))

    /*apply the schema to PharmaClass*/
    val mapToPharmaClass: KStream[String, PharmaClass] = eachLinesAsArray.mapValues { x =>
      try {

        PharmaClass(
          country =  x(0),
          year =  x(1),
          pcnt_health_expend = x(2),
          pcnt_gdp = x(3),
          usd_capital_expend = x(4),
          flag_codes = x(5),
          total_expend = x(6)
        )
      }
      catch {
        case e: Exception =>
          log.warn(s"Unable to apply schema: $e")

      }
    }

    /*sum of expenditure on health per per country returned as KTable, natively supported by DSL*/
    val pcnt_health_ex: KTable[String, String] = mapToPharmaClass
      /*group based on country and aggregate to find sum of pcnt_health_expend*/
      .groupBy((_,pharmaclass) => pharmaclass.country)
      .aggregate(
        () => => 0,
      (_, pharmaClass:PharmaClass, count: Int) => pharmaClass.pcnt_health_expend + count,
      Materialized.as("expenditure percent on health per country ")
    )
    /*send the final data to output kafka topic*/
    pcnt_health_ex.toStream.to("pcnt_health_expend")


    /*sum of pcnt_gdp in year 2015 per country returned as KTable, natively supported by DSL*/
    val pcnt_gdp : KTable[String, String] = mapToPharmaClass
      /*filter the data to find the percent of gdp only for 2015*/
      .filter((_,pharmaclass) => pharmaclass.year == 2015)
      /*group based on country and aggregate to find sum of pcnt_gdp*/
      .groupBy((_,pharmaclass) => pharmaclass.country)
      .aggregate(
        () => => 0,
      (_, pharmaClass:PharmaClass, count: Int) => pharmaClass.pcnt_gdp + count,
      Materialized.as("expenditure percent of GDP per country ")
    )
    /*send the final data to output kafka topic*/
    pcnt_health_ex.toStream.to("pcnt_gdp")

    /*streams are created here using DSL*/
    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
    /*starts streaming*/
    streams.start()

    /*shutdown the thread for stream*/
    sys.ShutdownHookThread {
      streams.close(10, TimeUnit.SECONDS)
    }
  }
}


name := "Kafka-streams"

version := "0.0.1"

scalaVersion := "2.12.8"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "apache snapshots" at "http://repository.apache.org/snapshots/",
  "confluent.io" at "http://packages.confluent.io/maven/"
)

libraryDependencies ++= Seq(
    "org.apache.kafka" %% "kafka-streams-scala" % "2.0.0",
    "io.confluent" % "kafka-streams-avro-serde" % "5.1.0",
    "com.sksamuel.avro4s" %% "avro4s-core" % "1.9.0"
    )