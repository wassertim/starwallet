package service.common

import model._


trait Starbucks {
  def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]]
  def authenticate(authInfo: AuthInfo): Either[Page, AuthError]
}
