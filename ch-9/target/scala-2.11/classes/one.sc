def toInteger(a: String): Option[Int] = {
  try {
    Some(Integer.parseInt(a.trim))
  } catch {
    case e: NumberFormatException => None
  }
}

val aa = toInteger("abcd") match {
  case Some(i) => println(i)
  case None => println("That didn't work.")
}
