package model

import java.sql.Timestamp
import play.api.libs.json.{JsPath, Writes}
import play.api.libs.functional.syntax._

case class Transaction(date: Timestamp, place: String, transactionType: String, amount: Double, balance: Double)
object Transaction {
  implicit val writes: Writes[Transaction] = (
    (JsPath \ "date").write[Timestamp] and
      (JsPath \ "place").write[String] and
      (JsPath \ "transactionType").write[String] and
      (JsPath \ "amount").write[Double] and
      (JsPath \ "balance").write[Double]
    )(unlift(Transaction.unapply))
}
