name := "New-Kafka-streams"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.6"

//val kafka_streams_scala_version = "0.1.0"

//libraryDependencies ++= Seq("com.lightbend" %% "kafka-streams-scala" % kafka_streams_scala_version)
resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "apache snapshots" at "http://repository.apache.org/snapshots/",
  "confluent.io" at "http://packages.confluent.io/maven/"
)

val kafka_streams_scala_version = "0.1.0"

libraryDependencies ++= Seq("com.lightbend" %%
  "kafka-streams-scala" % kafka_streams_scala_version)

