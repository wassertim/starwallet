package model

import java.sql.Timestamp
import play.api.libs.json.{Reads, JsPath, Writes}
import play.api.libs.functional.syntax._

case class CardListItem(
                         number: String,
                         balance: Double,
                         isActive: Boolean,
                         lastTransactionDate: Timestamp,
                         activationDate: Timestamp,
                         identity: AuthInfo
                         )

object CardListItem {

  import AuthInfo.writes

  implicit val writes: Writes[CardListItem] = (
    (JsPath \ "number").write[String] and
      (JsPath \ "balance").write[Double] and
      (JsPath \ "isActive").write[Boolean] and
      (JsPath \ "lastTransactionDate").write[Timestamp] and
      (JsPath \ "activationDate").write[Timestamp] and
      (JsPath \ "identity").write[AuthInfo]
    )(unlift(CardListItem.unapply))
}

case class CardData(number: String, pin: String)

object CardData {
  implicit val writes: Writes[CardData] = (
    (JsPath \ "number").write[String] and
      (JsPath \ "pin").write[String]
    )(unlift(CardData.unapply))
  implicit val reads: Reads[CardData] = (
    (JsPath \ "number").read[String] and
      (JsPath \ "pin").read[String]
    )(CardData.apply _)
}

case class Card(data: CardData, balance: Double, isActive: Boolean, transactions: Seq[Transaction])

object Card {

  import Transaction.writes

  implicit val writes: Writes[Card] = (
    (JsPath \ "data").write[CardData] and
      (JsPath \ "balance").write[Double] and
      (JsPath \ "isActive").write[Boolean] and
      (JsPath \ "transaction").write[Seq[Transaction]]
    )(unlift(Card.unapply))
}
