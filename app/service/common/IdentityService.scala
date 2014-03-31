package service.common

import model.AuthInfo

trait IdentityService {
  def get(id: Int): Option[AuthInfo]

  def add(info: AuthInfo, userId: Int): Int

  def list(userId: Int): Seq[AuthInfo]
}
