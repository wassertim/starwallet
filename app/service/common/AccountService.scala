package service.common

import model.AuthInfo

trait AccountService {
  def getAuthInfo(accountLogin: String, userId: Int): AuthInfo

}
