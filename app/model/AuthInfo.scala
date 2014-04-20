package model

case class AuthInfo(id: Int, userName: String, password: String)
case class RegAuthInfo(userName: String, password: String, email: String, firstName: String, lastName: String, cardNumber: String, pinCode: String, phoneNumber: String)
