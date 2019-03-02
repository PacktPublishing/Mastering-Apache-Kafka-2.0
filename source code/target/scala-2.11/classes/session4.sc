import com.sun.xml.internal.fastinfoset.sax.Properties

trait Thing
trait Vehicle extends Thing

class Car extends Vehicle
class Jeep extends Car
class Coupe extends Car
class Motorcycle extends Vehicle
class Vegetable

//class Parking[A](val place1: A, val place2: A)
//val obj = new Parking[Car](new Jeep, new Coupe)

class Parking[A <: Car](val place1: A)


//higher order function
//e.g. - map, reduce, flatMap, filter, etc.

val list = List(1,2,3,4,5,6,7)
val addOnetoEachElementOfList = list.map(x =>  x + 1)

println(addOnetoEachElementOfList)
val reduced = list.reduce(_+_)
//1 + 2 = 3
//3 + 3 = 6
//6 + 4 = 10
//10 + 5 = 15
//15 + 6 = 21
//21 + 7 = 28
val reduced1 = println(list.reduce((x,y) => (x+y)))


def ownMap(inp:Int) : Int = {
  val resutl = inp + 1
  println("resutl -> "+resutl)
    resutl
}

val mapWithOwnMap = list.map(ownMap)
def ownReduce(first:Int, second:Int) : Int ={
  val op = first + second
  println("first element, second element and addition is - "+first + "+" + second + "->" + op)
  op
}

def ownReduce1(first:String, second:String) : String ={
  val op = first + second
  println("first element, second element and addition is - "+first + "+" + second + "->" + op)
  op
}

val withOneReduce = list.reduce(ownReduce)

val strList = List("first","second","cthird")
//val reduceStr = strList.reduce(genericRed)
//list.reduce(genericRed)

def takeList(lst: List[String]) : String ={
  println(lst.head)
  lst.head
}

def takeList1(lst: List[Int]) : Int ={
  println(lst.head)
  lst.head
}

def takeCommonList[B] (lst: List[B]) : B = {
println(lst.head)
lst.head
}

takeCommonList(List(1,2,3,4))
takeCommonList(List("a","b","c"))

props.put("bootstrap.servers", "localhost:9092")
props.put("acks", "all")
props.put("retries", 0)
props.put("batch.size", 9999)
props.put("linger.ms", 1)
props.put("buffer.memory", 9999999)
props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer")
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
props.put("partitioner.class", "CustomPartitioner")

class CustomPartitioner extends Nothing {
  /* inherited method */ def configure(configs: Nothing): Unit = {
  }

  def partition(topic: String, //topic on which producer sends the data
                key: Any, //key based on which partition has to be decided
                keyBytes: Array[Byte], //serialized key
                value: Any, //value of the records
                valueBytes: Array[Byte], //serialized value
                cluster: Nothing): Int = { //current cluster where information of kafka topic, partitions etc are maintained
    /*get all partitions for the topic*/ val partitions = cluster.partitionsForTopic(topic)
    /*Get the number of partitions for the topic*/ val numPartitions = partitions.size
    /*return the partition based on key*/ if (key.asInstanceOf[Integer] < 10) {
      System.out.println("Key: " + key + " goes to partition: " + (numPartitions - 1))
      return numPartitions - 1
    }
    System.out.println("Key: " + key + " goes to partition: " + numPartitions)
    numPartitions
  }

  /*call the close method at the end*/
  def close(): Unit = {
  }
}


object ProducerWithPartitions {
  def main(args: Array[String]): Unit = {
    /*provide all the configuration properties to kafka*/
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("acks", "all")
    props.put("retries", 0)
    props.put("batch.size", 9999)
    props.put("linger.ms", 1)
    props.put("buffer.memory", 9999999)
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("partitioner.class", "CustomPartitioner")
    /*provide the topic to which records will be sent. Note that topic should be created before you send the data to it*/
    val topic = "testTopic"
    /*create an object of kafka producer by passing all the configuration properties*/
    val producer = new KafkaProducer<Integer, String> (props);
    /*initialize a producerRecord object that will actually hold the data in <key,value> pair, which would be sent to testTopic*/
    var producerRecord = null
    /*manually generate key and value to be sent to kafka topic using producerRecord. As per the configuration, key is Integer and value is String.*/
    /*This loop will go 10 times and first 4 keys will be always sent to second last partition based on CustomPartitioner logic*/
    var keys = 0
    while ( {
      keys < 10
    }) {
      /*generate key and value in a loop*/
      producerRecord = new Nothing(topic, new Integer(keys), "This is value for key no. " + keys)
      /*send each <key,value> pair to testTopic*/
      producer.send(producerRecord)
      System.out.println("Record no" + keys + "sent successfully")

      {
        keys += 1; keys - 1
      }
    }
    /*close the producer. Always remember to close the producer object otherwise connection leak may occur*/
    producer.close()
  }
}


bin/zookeeper-server-start.sh config/zookeeper.properties

Key: 0 goes to partition: 1
Record no: 0 sent successfully
Key: 1 goes to partition: 1
Record no: 1 sent successfully
Key: 2 goes to partition: 1
Record no: 2 sent successfully
Key: 3 goes to partition: 1
Record no: 3 sent successfully
Key: 4 goes to partition: 2
Record no: 4 sent successfully
Key: 5 goes to partition: 2
Record no: 5 sent successfully
Key: 6 goes to partition: 2
Record no: 6 sent successfully
Key: 7 goes to partition: 2
Record no: 7 sent successfully
Key: 8 goes to partition: 2
Record no: 8 sent successfully
Key: 9 goes to partition: 2
Record no: 9 sent successfully

key from producer - 0, value from producer - This is value for key no. 0, record belongs to which partition - 1, offset id of record - 11
key from producer - 1, value from producer - This is value for key no. 1, record belongs to which partition - 1, offset id of record - 12
key from producer - 2, value from producer - This is value for key no. 2, record belongs to which partition - 1, offset id of record - 13
key from producer - 3, value from producer - This is value for key no. 3, record belongs to which partition - 1, offset id of record - 14
key from producer - 4, value from producer - This is value for key no. 4, record belongs to which partition - 2, offset id of record - 15
key from producer - 5, value from producer - This is value for key no. 5, record belongs to which partition - 2, offset id of record - 16
key from producer - 6, value from producer - This is value for key no. 6, record belongs to which partition - 2, offset id of record - 17
key from producer - 7, value from producer - This is value for key no. 7, record belongs to which partition - 2, offset id of record - 18
key from producer - 8, value from producer - This is value for key no. 8, record belongs to which partition - 2, offset id of record - 19
key from producer - 9, value from producer - This is value for key no. 9, record belongs to which partition - 2, offset id of record - 20


String BOOTSTRAP_SERVERS = “<IP>:9092, <IP>:9093, <IP>:9094”;
properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS);


bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic testTopic --group KafkaConsumerExample –partition 1
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic testTopic --group KafkaConsumerExample –partition 2
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic testTopic --group KafkaConsumerExample –partition 3




