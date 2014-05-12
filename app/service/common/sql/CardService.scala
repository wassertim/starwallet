package service.common.sql

import model._

trait CardService {
  def savePin(pinCode: String, number: String): Unit

  def savePin(card: CardData): Unit = savePin(card.pin, card.number)

  def get(number: String, userId: Int): Option[Card]

  def listByUser(userId: Int): Seq[CardListItem]

  def getTransactions(cardNumber: String): Seq[Transaction]

  def listByIdentity(id: Int): Seq[CardListItem]
}
