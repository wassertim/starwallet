package service.fake.http

import scala.concurrent.Future
import model.RegistrationInfo

class RegistrationService extends service.common.http.RegistrationService {
  override def register(info: RegistrationInfo): Future[Either[Unit, String]] = ???

  override def activate(url: String): Future[Either[Unit, String]] = ???
}
