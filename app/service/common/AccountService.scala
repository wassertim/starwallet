package service.common

import model._

trait AccountService {
  def sync(account: StarbucksAccount[Card], id: Int)

  def get(id: Int): Option[StarbucksAccount[CardListItem]]
}
