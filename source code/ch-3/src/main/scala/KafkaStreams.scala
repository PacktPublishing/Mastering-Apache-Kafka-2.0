import java.lang.Long
import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.kstream.{KGroupedStream, KStream, KTable, Materialized}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import org.apache.kafka.common.serialization.Serdes
import Serdes._

import scala.language.implicitConversions
import scala.util.Try

case class PharmaClass(val country: String,
                       val year: String,
                       val pcnt_health_expend: String,
                       val pcnt_gdp: String,
                       val usd_capital_expend: String,
                       val flag_codes: String,
                       val total_expend: String)


object KafkaStreams {
  def main(args: Array[String]): Unit = {

    val config: Properties = {
      val p = new Properties()
      p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application")
      p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
      p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
      p
    }

    //implicit val pharmaClassSerde: Serde[PharmaClass] = new AvroSerde

    val builder: StreamsBuilder = new StreamsBuilder

    val textLines: KStream[String, String] = builder.stream[String, String]("pharma")
    val eachLinesAsArray : KStream[String, Array[String]]  = textLines
        .mapValues(x => x.split(","))

    val mapToPharmaClass: KStream[String, PharmaClass] = eachLinesAsArray.
      mapValues { x =>
      {

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
    }

    /*sum of expenditure on health per per country*/
    val pcnt_health_ex : KTable[String, Double] = mapToPharmaClass.mapValues( y => (y.country,y.pcnt_health_expend.toDouble))
      .groupByKey()
      .reduce(_+_)
    pcnt_health_ex.toStream.to("pcnt_health_expend")

    /*sum of pcnt_gdp in year 2015 per country*/
    val pcnt_gdp : KTable[String, Double] = mapToPharmaClass
      .filter((_,pharmaclass) => pharmaclass.year == 2015)
      .mapValues( y => (y.country,y.pcnt_gdp.toDouble))
      .groupByKey()
        .reduce(_+_)
    pcnt_health_ex.toStream.to("pcnt_gdp")


    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
    streams.start()

    sys.ShutdownHookThread {
      streams.close(10, TimeUnit.SECONDS)
    }
  }
  }