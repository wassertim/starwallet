package model

import play.api.libs.json._
import play.api.libs.functional.syntax._
import AuthInfo.reads
import CardData.reads

case class RegistrationInfo(auth: AuthInfo, email: String, firstName: String, lastName: String, card: CardData, phoneNumber: String)
object RegistrationInfo {
  implicit val reads: Reads[RegistrationInfo] = (
    (JsPath \ "auth").read[AuthInfo] and
      (JsPath \ "email").read[String] and
      (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "card").read[CardData] and
      (JsPath \ "phoneNumber").read[String]
    )(RegistrationInfo.apply _)
}