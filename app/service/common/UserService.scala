package service.common

import model.{Identity, AuthInfo}

trait UserService {
  def register[T](user: AuthInfo, onSuccess: Int => T, onError: String => T): T
  def authenticate(auth: AuthInfo): Identity = authenticate(auth.userName, auth.password)
  def authenticate(login: String, password: String): Identity
}
