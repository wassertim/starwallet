package service.fake.http

import model._
import utility.DateTimeUtility
import org.joda.time.DateTime
import scala.util.Random

class Starbucks extends service.common.http.Starbucks {

  override def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]] = Some {
    val rnd = Seq.fill(4)(Random.nextInt(10))
    StarbucksAccount(
      "tim",
      5,
      Seq(
        Card(
          CardData(s"72801546${rnd.mkString("")}", "454545"),
          200,
          isActive = true,
          Seq(Transaction(DateTimeUtility.ts(DateTime.now()), "New York", "type", 200, 400))
        )
      ),
      Seq(Coupon("23234234", DateTimeUtility.ts(DateTime.now()), DateTimeUtility.ts(DateTime.now()), isActive = true, "type", "key")),
      DateTimeUtility.ts(DateTime.now()),
      Some("test@test.com")
    )
  }

  def authenticate(authInfo: AuthInfo) = Left()

}
