package service.fake

import model.AuthInfo

class AccountService extends service.common.AccountService {
  override def getAuthInfo(accountLogin: String, userId: Int): AuthInfo = AuthInfo("test", "test")
}
