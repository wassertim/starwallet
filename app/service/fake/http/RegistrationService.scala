package service.fake.http

import scala.concurrent.Future
import model.RegistrationInfo
import play.api.libs.concurrent.Execution.Implicits._

class RegistrationService extends service.common.http.RegistrationService {
  override def register(info: RegistrationInfo): Future[Either[Unit, String]] = Future {
    Left()
  }
}
