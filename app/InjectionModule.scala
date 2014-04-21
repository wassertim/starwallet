import service._
import service.sql._
import com.tzavellas.sse.guice.ScalaModule

class InjectionModule extends ScalaModule {
  def configure() = {
    bind[service.common.Starbucks].toInstance(new Starbucks)
    bind[service.common.UserService].toInstance(new UserService)
    bind[service.common.IdentityService].toInstance(new IdentityService)
    bind[service.common.AccountService].toInstance(new AccountService(new CardService))
    bind[service.common.CardService].toInstance(new CardService)
    bind[service.common.CouponService].toInstance(new CouponService)
    bind[service.common.RegistrationService].toInstance(new RegistrationService)
  }
}
