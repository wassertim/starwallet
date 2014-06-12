package service.common.http

import model._
import scala.concurrent.Future


trait Starbucks {
  def register(info: RegistrationInfo): Future[Either[Unit, String]]
  def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]]
  def authenticate(authInfo: AuthInfo): Either[Unit, AuthError]
  def activate(url: String): Future[Either[Unit, String]]
}
