package model

import java.sql.Timestamp

object Joda {
  implicit def dateTimeOrdering: Ordering[Timestamp] = Ordering.fromLessThan(_ before _)
}
case class Card(number: String, balance: Double, isActive: Boolean, transactions: Seq[Transaction]) {


  def lastTransactionDate = {
    import Joda._

    transactions.sortBy(_.date)(Ordering[Timestamp].reverse).head.date
  }

  def activationDate = {
    import Joda._

    transactions.sortBy(_.date)(Ordering[Timestamp]).head.date
  }
}
