package service.common.sql

import model._

trait IdentityService {
  def register(info: RegistrationInfo, userId: Int): Int

  def getOwnerId(identityId: Int): Int

  def remove(id: Int, userId: Int)

  def update(auth: AuthInfo, userId: Int)

  def get(id: Int): Option[AuthInfo]

  def add(info: AuthInfo, userId: Int): Int

  def list(userId: Int): Seq[IdentityListItem]

  def listAuth(userId: Int): Seq[AuthInfo]

  def encryptAllPasswords
}
