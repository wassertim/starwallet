package model

import java.sql.Timestamp

case class CardListItem (
  number: String,
  balance: Double,
  isActive: Boolean,
  lastTransactionDate: Timestamp,
  activationDate: Timestamp,
  identity: AuthInfo
)
case class CardData(number: String, pin: String)
case class Card(data: CardData, balance: Double, isActive: Boolean, transactions: Seq[Transaction])
