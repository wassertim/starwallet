package service.fake

import model._

class IdentityService extends service.common.IdentityService {

  def list(userId: Int) = List(IdentityListItem(1, "test1", 1,1, null), IdentityListItem(2, "test2", 1,1, null))

  def add(info: AuthInfo, userId: Int): Int = 1

  def get(id: Int, userId: Int): Option[AuthInfo] = ???

  override def update(auth: AuthInfo, userId: Int): Unit = ???

  override def remove(id: Int, userId: Int): Unit = ???

  override def encryptAllPasswords: Unit = ???
}
