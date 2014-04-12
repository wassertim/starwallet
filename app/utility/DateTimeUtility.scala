package utility

import org.joda.time.DateTime
import java.sql.Timestamp
import model.Transaction
object Joda {
  implicit def dateTimeOrdering: Ordering[Timestamp] = Ordering.fromLessThan(_ before _)
}
object DateTimeUtility {
  import Joda._
  def ts(date: DateTime): Timestamp = new Timestamp(date.toDate.getTime)
  def lastTransactionDate(transactions: Seq[Transaction]) = {
    transactions.sortBy(_.date)(Ordering[Timestamp].reverse).head.date
  }

  def activationDate(transactions: Seq[Transaction]) = {
    transactions.sortBy(_.date)(Ordering[Timestamp]).head.date
  }
}
