package model

import play.api.libs.json.{JsPath, Writes}
import play.api.libs.functional.syntax._

case class User(userId: Int, userName: String, isAuthenticated: Boolean, role: String = "user")

object User {
  implicit val writes: Writes[User] = (
    (JsPath \ "userId").write[Int] and
      (JsPath \ "userName").write[String] and
      (JsPath \ "isAuthenticated").write[Boolean] and
      (JsPath \ "role").write[String]
    )(unlift(User.unapply))
}
