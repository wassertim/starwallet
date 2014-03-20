package specs

import org.specs2.mutable.Specification
import com.google.inject.Guice
import specs.util.InjectionModule

class AccountService extends Specification {


  private lazy val injector = Guice.createInjector(new InjectionModule)
  "AccountService" should {
    "return authInfo" in {
      val accountService = injector.getInstance(classOf[service.common.AccountService])
      val starbucksService = injector.getInstance(classOf[service.common.Starbucks])
      val accountLogin = "testLogin"
      val userId = 1
      val authInfo = accountService.getAuthInfo(accountLogin, userId)
      val starbucksAccount = starbucksService.getAccountData(authInfo)
      starbucksAccount.cards.size must be greaterThan 0
    }
  }
}
