package model

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Login(userName: String, password: String)

object Login {
  implicit val reads: Reads[Login] = (
    (JsPath \ "userName").read[String] and
      (JsPath \ "password").read[String]
    )(Login.apply _)
}
