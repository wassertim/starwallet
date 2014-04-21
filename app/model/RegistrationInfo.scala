package model

case class RegistrationInfo(auth: AuthInfo, email: String, firstName: String, lastName: String, card: CardData, phoneNumber: String)
