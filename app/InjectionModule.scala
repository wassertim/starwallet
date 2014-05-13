import service.http._
import service.pop._
import service.sql._
import com.tzavellas.sse.guice.ScalaModule
import play.api.Play
import Play.{current => p}

class InjectionModule extends ScalaModule {
  def configure() = {

    bind[service.common.sql.UserService].toInstance(new UserService)
    bind[service.common.sql.IdentityService].toInstance(new IdentityService)
    bind[service.common.sql.AccountService].toInstance(new AccountService(new CardService))
    bind[service.common.sql.CardService].toInstance(new CardService)
    bind[service.common.sql.CouponService].toInstance(new CouponService)

    bind[service.common.http.RegistrationService].toInstance(new RegistrationService)
    bind[service.common.http.Starbucks].toInstance(new Starbucks)
    val email = p.configuration.getString("activation.email").get
    val password = p.configuration.getString("activation.password").get
    bind[service.common.pop.EmailClient].toInstance(new EmailClient(email, password))
  }
}
