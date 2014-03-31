package service.common

import model._

trait UserService {
  def register[T](user: Login, onSuccess: Int => T, onError: String => T): T
  def authenticate(auth: Login): Identity = authenticate(auth.userName, auth.password)
  def authenticate(login: String, password: String): Identity
}
