package service.fake

import model.{Transaction, Coupon, StarbucksAccount}
import org.joda.time.DateTime

class Starbucks extends service.common.Starbucks {
  override def auth(userName: String, password: String): StarbucksAccount = StarbucksAccount(
    "899878873423",
    3,
    5,
    "Active",
    List(Coupon("3443233223", DateTime.now, DateTime.now, "active", "registration")),
    List(Transaction(DateTime.now, "Rockefeller Center", "pay", 45))
  )
}
