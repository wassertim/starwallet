package model

case class AuthInfo(id: Int, userName: String, password: String, activationEmail: Option[String] = None, isActive: Boolean = false)

