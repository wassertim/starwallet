package specs

import org.specs2.mutable.Specification
import com.google.inject.Guice
import specs.util.InjectionModule
import service.common._
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean
import service.common.http.Starbucks
import service.common.sql.IdentityService

class AccountServiceSpec extends Specification {


  private lazy val injector = Guice.createInjector(new InjectionModule)
  "AccountService" should {
    "return authInfo" in {
      val accountService = injector.getInstance(classOf[IdentityService])
      val starbucksService = injector.getInstance(classOf[Starbucks])
      val accountLogin = "testLogin"
      val userId = 1
      accountService.get(userId, userId) match {
        case Some(authInfo) =>
          val starbucksAccount = starbucksService.getAccount(authInfo).get
          starbucksAccount.cards.size must be greaterThan 0
        case _ => 0 must be equalTo 1
      }
    }
    "return list of authInfo" in {
      val accountService = injector.getInstance(classOf[IdentityService])
      val userId = 1
      val list = accountService.list(userId)
      list.size must be equalTo 2
    }

  }
}
