package model

import org.joda.time.DateTime
import java.sql.Timestamp
import play.api.libs.json.{JsPath, Writes}
import play.api.libs.functional.syntax._

case class Coupon(number:String, issueDate: Timestamp, expirationDate: Timestamp, isActive: Boolean, couponType: String, key: String)
object Coupon {
  implicit val writes: Writes[Coupon] = (
    (JsPath \ "number").write[String] and
      (JsPath \ "issueDate").write[Timestamp] and
      (JsPath \ "expirationDate").write[Timestamp] and
      (JsPath \ "isActive").write[Boolean] and
      (JsPath \ "couponType").write[String] and
      (JsPath \ "key").write[String]
    )(unlift(Coupon.unapply))
}