package model

import java.sql.Timestamp
import play.api.libs.json.{JsPath, Writes}
import play.api.libs.functional.syntax._

case class IdentityListItem(id: Int, userName: String, starsCount: Int, activeCouponsCount: Int, lastUpdate: Timestamp, isActive: Boolean)
object IdentityListItem {
  implicit val writes: Writes[IdentityListItem] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "userName").write[String] and
      (JsPath \ "starsCount").write[Int] and
      (JsPath \ "activeCouponsCount").write[Int] and
      (JsPath \ "lastUpdate").write[Timestamp] and
      (JsPath \ "isActive").write[Boolean]
    )(unlift(IdentityListItem.unapply))
}