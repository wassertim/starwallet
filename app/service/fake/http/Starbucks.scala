package service.fake.http

import model._
import utility.DateTimeUtility
import org.joda.time.DateTime
import scala.util.Random
import java.sql.Timestamp
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future



case class AccountWrapper(var isActive: Boolean, account: StarbucksAccount[Card])
object Starbucks {
  val accounts: Seq[AccountWrapper] = (1 to 100).map { index =>
    AccountWrapper(
      isActive = false,
      StarbucksAccount(
        s"tim_$index",
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
    )
  }
}
class Starbucks extends service.common.http.Starbucks {
  import Starbucks.accounts

  def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]] = {
    val now = new Timestamp(new java.util.Date().getTime)
    authenticate(authInfo).fold(
      page => {
        Some(accounts.find(a => a.account.userName == authInfo.userName).get.account)
      },
      error => None
    )
  }

  def authenticate(authInfo: AuthInfo) = {
    accounts.find(a => a.account.userName == authInfo.userName) match {
      case Some(t) => if (t.isActive) Left() else Right(AuthError("Error"))
      case None => Right(AuthError("Error"))
    }
  }

  override def activate(url: String): Future[Either[Unit, String]] = Future {
    accounts.find(a => a.account.userName == url) match {
      case Some(t) => t.isActive = true; Left()
      case _ => Right("Error")
    }
  }
}
