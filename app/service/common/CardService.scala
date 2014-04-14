package service.common

import model._

trait CardService {
  def get(number: String, userId: Int): Option[Card]

  def listByUser(userId: Int): Seq[CardListItem]

  def getTransactions(cardNumber: String): Seq[Transaction]

  def listByIdentity(id: Int): Seq[CardListItem]
}
