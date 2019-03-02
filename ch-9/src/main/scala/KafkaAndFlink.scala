object KafkaAndFlink {
  def main(args: Array[String]) {

    /*obtain an execution enrionment*/
    val exec_env = StreamExecutionEnvironment.getExecutionEnvironment()

    /*provides kafka parameters*/
    val kafkaParameters = Map[String, String](
      "bootstrap.servers" -> "localhost:9092",
      "zookeeper.connect" -> "localhost:2181",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.AutoOffsetRest -> "kafka",
      "group.id" -> "consumerGroup",
      ConsumerConfig.AutoCommit.toString -> "false"
    )

    /*pass topic name*/
    val topics = Set("pharma")
    /*define source, which is kafka in this case.
      need to provide the connector - flink-connector-kafka-0.10_2.11 in build.sbt file*/
    val consumerForOffset = new FlinkKafkaConsumer010[String](topics, new SimpleStringSchema(), kafkaParameters)
    /*read the offset from earliest*/
    consumerForOffset.setStartFromEarliest()
    val dataStream = exec_env.addSource(consumerForOffset)

    /*print raw stream*/
    dataStream.print()

    /*count the GDP per country in a window of 10 seconds*/
    val gdpPerCountry = dataStream
                      .keyBy(0) //partition by country
                      .timeWindow(Time.seconds(10)) //find the result based on time window of 10 seconds
                      .apply(
                         (val1, val2) => val2,
                         (key, window, vals, c: Collector[(String, Long)]) => {
                            if (vals.head != null) c.collect((vals.head.state, 1))
                          }
                      )
    /*print the gdpPerCountry*/
    gdpPerCountry.print

    /*trigger the program execution*/
    exec_env.execute("Kafka Window Stream WordCount")
  }
}
