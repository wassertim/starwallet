package service.common

import model.StarbucksAccount

trait AccountService {
  def sync(account: StarbucksAccount, id: Int)

  def get(id: Int): Option[StarbucksAccount]
}
