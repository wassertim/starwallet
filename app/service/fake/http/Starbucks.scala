package service.fake.http

import model._

class Starbucks extends service.common.http.Starbucks {

  override def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]] = ???

  override def authenticate(authInfo: AuthInfo): Either[Page, AuthError] = ???

}
