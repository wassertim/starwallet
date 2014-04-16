package service.common

import model._

trait IdentityService {
  def remove(id: Int, userId: Int)

  def update(auth: AuthInfo, userId: Int)

  def get(id: Int, userId: Int): Option[AuthInfo]

  def add(info: AuthInfo, userId: Int): Int

  def list(userId: Int): Seq[IdentityListItem]

  def encryptAllPasswords
}
