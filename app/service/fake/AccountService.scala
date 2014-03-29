package service.fake

import model.AuthInfo

class AccountService extends service.common.AccountService {

  def list(userId: Int): Seq[AuthInfo] = List(AuthInfo(1, "test1", "test1"), AuthInfo(2, "test2", "test2"))

  def add(info: AuthInfo, userId: Int): Int = 1

  def get(id: Int): Option[AuthInfo] = ???
}
