package service.common.sql

import model._

trait UserService {
  def saveSettings(settings: UserSettings, userId: Int)
  def getSettings(userId: Int): Option[UserSettings]
  def register[T](user: Login, onSuccess: Int => T, onError: String => T): T
  def authenticate(auth: Login): User = authenticate(auth.userName, auth.password)
  def authenticate(login: String, password: String): User
}
