package service.fake.http

import model._
import utility.DateTimeUtility
import org.joda.time.DateTime
import scala.util.Random

class Starbucks extends service.common.http.Starbucks {

  override def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]] = Some {
    StarbucksAccount(
      "tim",
      5,
      Seq(
        Card(
          CardData(s"72801546${Seq.fill(4)(Random.nextInt(10)).mkString("")}", "454545"),
          200,
          isActive = true,
          Seq(Transaction(DateTimeUtility.ts(DateTime.now()), "New York", "type", 200, 400))
        )
      ),
      Seq(Coupon(
        s"${Seq.fill(8)(Random.nextInt(10)).mkString("")}",
        DateTimeUtility.ts(DateTime.now()),
        DateTimeUtility.ts(DateTime.now()),
        isActive = true, "type", "key")),
      DateTimeUtility.ts(DateTime.now()),
      Some("test@test.com")
    )
  }

  def authenticate(authInfo: AuthInfo) = Left()

}
