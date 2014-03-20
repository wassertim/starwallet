package service.common

import model.AuthInfo

trait AccountService {
  def list(userId: Int): Seq[AuthInfo]

  def getAuthInfo(accountLogin: String, userId: Int): AuthInfo

}
