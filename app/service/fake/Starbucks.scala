package service.fake

import model._
import org.joda.time.DateTime
import java.sql.Timestamp

class Starbucks extends service.common.Starbucks {
  def getAccountData(authInfo: AuthInfo): StarbucksAccount = {
    val now = new Timestamp(new java.util.Date().getTime)
    StarbucksAccount(
      authInfo.userName,
      3,
      List(Card("899878873423", 5, isActive = true, "", List(Transaction(now, "Rockefeller Center", "pay", 45, 45)))),
      List(Coupon("3443233223", now, now, isActive = true, "registration", "")),
      now
    )
  }

  override def getAccount(authInfo: AuthInfo): Option[StarbucksAccount] = ???

  override def authenticate(authInfo: AuthInfo): Either[Page, AuthError] = ???
}
