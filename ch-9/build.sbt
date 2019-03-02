name := "SparkJobs"

version := "1.0"

scalaVersion := "2.11.6"

val sparkVersion = "2.3.0"

resolvers ++= Seq(
  "apache snapshots" at "http://repository.apache.org/snapshots/"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,  
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion,  
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "org.apache.flink" %% "flink-connector-kafka-0.10" % sparkVersion  
)

