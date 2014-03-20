package service.fake

import model._
import org.joda.time.DateTime

class Starbucks extends service.common.Starbucks {
  override def getAccountData(authInfo: AuthInfo): StarbucksAccount = StarbucksAccount(
    authInfo.userName,
    3,
    List(Card("899878873423", 5, isActive = true, List(Transaction(DateTime.now, "Rockefeller Center", "pay", 45, 45)))),
    List(Coupon("3443233223", DateTime.now, DateTime.now, isActive = true, "registration"))
  )
}
