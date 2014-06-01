package model

import play.api.libs.json.{JsPath, Writes}
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
}

