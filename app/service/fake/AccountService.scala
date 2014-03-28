package service.fake

import model.AuthInfo

class AccountService extends service.common.AccountService {
  override def getAuthInfo(accountLogin: String, userId: Int): AuthInfo = AuthInfo("test", "test")

  override def list(userId: Int): Seq[AuthInfo] = List(AuthInfo("test1", "test1"), AuthInfo("test2", "test2"))

  override def add(info: AuthInfo): Int = 1
}
