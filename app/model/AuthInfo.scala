package model

import play.api.libs.json.{Reads, JsPath, Writes}
import play.api.libs.functional.syntax._

case class AuthInfo(id: Int, userName: String, password: String, activationEmail: Option[String] = None, isActive: Boolean = false)
object AuthInfo {
  implicit val writes: Writes[AuthInfo] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "userName").write[String] and
      (JsPath \ "password").write[String] and
      (JsPath \ "activationEmail").write[Option[String]] and
      (JsPath \ "isActive").write[Boolean]
    )(unlift(AuthInfo.unapply))

  implicit val reads: Reads[AuthInfo] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "userName").read[String] and
      (JsPath \ "password").read[String] and
      (JsPath \ "activationEmail").read[Option[String]] and
      (JsPath \ "isActive").read[Boolean]
    )(AuthInfo.apply _)
}

