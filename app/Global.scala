import com.google.inject.Guice
import com.tzavellas.sse.guice.ScalaModule
import play.api.GlobalSettings
import service.fake.Starbucks
import service.sql.UserService


object Global extends GlobalSettings {

  class InjectionModule extends ScalaModule {
    def configure() = {
      bind[service.common.Starbucks].toInstance(new Starbucks)
      bind[service.common.UserService].toInstance(new UserService)
    }
  }

  private lazy val injector = Guice.createInjector(new InjectionModule)

  override def getControllerInstance[A](controllerClass: Class[A]) = {
    injector.getInstance(controllerClass)
  }
}

