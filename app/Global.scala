import com.google.inject.Guice
import com.tzavellas.sse.guice.ScalaModule
import play.api.GlobalSettings
import service.sql._
import service.Starbucks


object Global extends GlobalSettings {

  class InjectionModule extends ScalaModule {
    def configure() = {
      bind[service.common.Starbucks].toInstance(new Starbucks)
      bind[service.common.UserService].toInstance(new UserService)
      bind[service.common.IdentityService].toInstance(new IdentityService)
      bind[service.common.AccountService].toInstance(new AccountService)
      bind[service.common.CardService].toInstance(new CardService)
      bind[service.common.CouponService].toInstance(new CouponService)
    }
  }

  private lazy val injector = Guice.createInjector(new InjectionModule)

  override def getControllerInstance[A](controllerClass: Class[A]) = {
    injector.getInstance(controllerClass)
  }
}

