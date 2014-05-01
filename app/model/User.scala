package model

case class User(userId: Int, userName: String, isAuthenticated: Boolean, role: String = "user")
