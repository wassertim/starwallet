package specs.util

import com.tzavellas.sse.guice.ScalaModule
import service.fake.http.Starbucks
import service.sql.IdentityService

class InjectionModule extends ScalaModule {
  def configure() = {
    bind[service.common.http.Starbucks].toInstance(new Starbucks)
    bind[service.common.sql.IdentityService].toInstance(new IdentityService)
  }
}
