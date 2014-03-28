package service.common

import model.AuthInfo

trait AccountService {
  def add(info: AuthInfo): Int

  def list(userId: Int): Seq[AuthInfo]

  def getAuthInfo(accountLogin: String, userId: Int): AuthInfo

}
