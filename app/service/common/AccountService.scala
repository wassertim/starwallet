package service.common

import model.AuthInfo

trait AccountService {
  def get(id: Int): Option[AuthInfo]

  def add(info: AuthInfo, userId: Int): Int

  def list(userId: Int): Seq[AuthInfo]
}
