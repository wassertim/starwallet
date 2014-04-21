package service.common

import model.RegistrationInfo

trait RegistrationService {
  def register(info: RegistrationInfo)
}
