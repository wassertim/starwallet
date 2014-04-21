package service.fake

import model._
import org.joda.time.DateTime
import java.sql.Timestamp

class Starbucks extends service.common.Starbucks {

  override def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]] = ???

  override def authenticate(authInfo: AuthInfo): Either[Page, AuthError] = ???

}
