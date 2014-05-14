package service.common.http

import model._
import scala.concurrent.Future

trait RegistrationService {
  def register(info: RegistrationInfo): Future[Either[Unit, String]]
}