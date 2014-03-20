package specs

import org.specs2.mutable.Specification
import com.google.inject.Guice
import specs.util.InjectionModule
import service.common._

class AccountServiceSpec extends Specification {


  private lazy val injector = Guice.createInjector(new InjectionModule)
  "AccountService" should {
    "return authInfo" in {
      val accountService = injector.getInstance(classOf[AccountService])
      val starbucksService = injector.getInstance(classOf[Starbucks])
      val accountLogin = "testLogin"
      val userId = 1
      val authInfo = accountService.getAuthInfo(accountLogin, userId)
      val starbucksAccount = starbucksService.getAccountData(authInfo)
      starbucksAccount.cards.size must be greaterThan 0
    }
    "return list of authInfo" in {
      val accountService = injector.getInstance(classOf[AccountService])
      val userId = 1
      val list = accountService.list(userId)
      list.size must be equalTo 2
    }
  }
}
