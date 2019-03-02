import scala.util.{Failure, Success, Try}
val lst = List(1,2,3,4)

for(i <- lst) {
  if (i % 2 == 0)
    print(i)
}

for(x <- lst if x > 2 )
  print(x)

val a = for(x <- lst if x > 3 ) yield x
a

/*******try/catch*****/
//compile-time error
val aa: Int = 12

//run-time error
try {
  if (2 / 1 == 0)
    print("error")
  else
    println("more error\n")
}
catch{
  case e: ArithmeticException => println("exception caught - "+e.getMessage)
  //case _ => print("will catch all exceptions")
}
finally {
  print("control will come here always")
}
/******** Try **********/
 val tryy = Try(4 / 0)
tryy match {
  case Success(x) => println("true-"+x)
  case Failure(x) => println("false-"+x)
}

val tryyy = Try(4 / 0).isSuccess

/*** Pattern Matching *****/
val aaa = 10
aaa match {
  case a: Int => println("aaa is an integer-->"+a)
  case _ => println("aaa is not integer")
}
//Option, Some, None

def toInteger(a: String): Option[Int] = {
  try {
    Some(Integer.parseInt(a.trim))
  } catch {
    case e: NumberFormatException => None
  }
}

val bb = toInteger("1234") match {
  case Some(i) => println(i)
  case None => println("That didn't work.")
}
/*** tuple ****/
val tple = (1,2,"abcd")
tple._3

/** anonymous function ****/
//it takes one element from the source at a time
//and outputs the result one by one
//(ea(each_input_element => <your logic/condition>)

def myFunc(a:String, b: String) : Boolean ={
  println("each element coming as : key--> " + a)
  println("each element coming as : value--> " + b)
  a > "a"
}

val myList = List(1,2,3)
/***** Maps *****/

val map = Map("a" -> "first",
              "b" -> "second",
              "c" -> "third")
//"a" -> "first"
map.count(x => x._1 > "a")
map.count(x => myFunc(x._1, x._2))
map.getOrElse("b","key not found")


/******** partially applied function **********/
def partSum(a:Int, b: Int, c:Int) = a + b + c
partSum(2,3,4)
val b = partSum(2,_:Int,4)
b(3)

/******* repteated parametes ********/
def repParam(words: String*) ={
  for (word <- words)
    println("each worrd - "+word)
}

repParam("hello")
repParam("hello", "world")

/****** default parameter *****/
def defParam(a: Int, b:Int):Float = a / b
defParam(4,3)








