package service.common.http

import model._


trait Starbucks {
  def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]]
  def authenticate(authInfo: AuthInfo): Either[Unit, AuthError]
}
