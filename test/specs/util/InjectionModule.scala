package specs.util

import com.tzavellas.sse.guice.ScalaModule
import service.fake._

class InjectionModule extends ScalaModule {
  def configure() = {
    bind[service.common.Starbucks].toInstance(new Starbucks)
    bind[service.common.IdentityService].toInstance(new IdentityService)
  }
}
