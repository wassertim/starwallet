val text = "You have 2 stars"
val pattern = "\\d+".r
val result = text match {
  case pattern(x) => x.toInt
  case _ => 0
}
pattern findFirstIn text
object Status extends Enumeration {
  type Status = Value
  val active, expired, used = Value
}

import Status._
val e = active
val z = expired
e == z