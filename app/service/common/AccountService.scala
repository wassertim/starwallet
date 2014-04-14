package service.common

import model._

trait AccountService {
  def sync(account: StarbucksAccount, id: Int)

  def get(id: Int): Option[CachedAccount]
}
