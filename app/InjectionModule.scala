import service.fake.http._
import service.fake.pop._
import service.sql._
import com.tzavellas.sse.guice.ScalaModule

class InjectionModule extends ScalaModule {
  def configure() = {
    bind[service.common.sql.UserService].toInstance(new UserService)
    bind[service.common.sql.IdentityService].toInstance(new IdentityService)
    bind[service.common.sql.AccountService].toInstance(new AccountService(new CardService))
    bind[service.common.sql.CardService].toInstance(new CardService)
    bind[service.common.sql.CouponService].toInstance(new CouponService)

    bind[service.common.http.RegistrationService].toInstance(new RegistrationService)
    bind[service.common.http.Starbucks].toInstance(new Starbucks)
    bind[service.common.pop.EmailClient].toInstance(new EmailClient("", ""))
  }
}
