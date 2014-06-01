package model

import play.api.libs.json.{Reads, JsPath, Writes}
import play.api.libs.functional.syntax._

case class UserSettings(firstName: String, lastName: String, phone: String, emailDomain: String)
object UserSettings {
  implicit val writes: Writes[UserSettings] = (
    (JsPath \ "firstName").write[String] and
      (JsPath \ "lastName").write[String] and
      (JsPath \ "phone").write[String] and
      (JsPath \ "emailDomain").write[String]
    )(unlift(UserSettings.unapply))
  implicit val reads: Reads[UserSettings] = (
    (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "phone").read[String] and
      (JsPath \ "emailDomain").read[String]
    )(UserSettings.apply _)
}
