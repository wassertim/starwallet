import service.sql._
import com.tzavellas.sse.guice.ScalaModule
import play.api.Play
import Play.{current => p}

class InjectionModule extends ScalaModule {
  val email = p.configuration.getString("activation.email").get
  val password = p.configuration.getString("activation.password").get
  def configure() = {
    bind[service.common.sql.UserService].toInstance(new UserService)
    bind[service.common.sql.IdentityService].toInstance(new IdentityService)
    bind[service.common.sql.AccountService].toInstance(new AccountService(new CardService))
    bind[service.common.sql.CardService].toInstance(new CardService)
    bind[service.common.sql.CouponService].toInstance(new CouponService)
    p.configuration.getString("application.mode").get match {
      case "prod" => {
        import service.http._
        import service.pop._
        bind[service.common.http.RegistrationService].toInstance(new RegistrationService)
        bind[service.common.http.Starbucks].toInstance(new Starbucks)
        bind[service.common.pop.EmailClient].toInstance(new EmailClient(email, password))
      }
      case "dev" => {
        import service.fake.http._
        import service.fake.pop._
        bind[service.common.http.RegistrationService].toInstance(new RegistrationService)
        bind[service.common.http.Starbucks].toInstance(new Starbucks)
        bind[service.common.pop.EmailClient].toInstance(new EmailClient(email, password))
      }
    }

  }
}
