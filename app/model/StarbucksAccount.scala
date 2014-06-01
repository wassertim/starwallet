package model

import java.sql.Timestamp
import play.api.libs.json.{JsPath, Writes}
import play.api.libs.functional.syntax._
import CardListItem.writes
import Coupon.writes

case class StarbucksAccount[T](
  userName: String,
  starsCount: Int,
  cards: Seq[T],
  coupons: Seq[Coupon],
  syncDate: Timestamp,
  email: Option[String] = None
)
object StarbucksAccount {
  implicit val writesWithCardListItem: Writes[StarbucksAccount[CardListItem]] = (
    (JsPath \ "userName").write[String] and
      (JsPath \ "starsCount").write[Int] and
      (JsPath \ "cards").write[Seq[CardListItem]] and
      (JsPath \ "coupons").write[Seq[Coupon]] and
      (JsPath \ "syncDate").write[Timestamp] and
      (JsPath \ "email").write[Option[String]]
    )(unlift(StarbucksAccount.unapply[CardListItem]))
}
