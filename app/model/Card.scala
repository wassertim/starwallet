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
case class Card(number: String, balance: Double, isActive: Boolean, pinCode: String, transactions: Seq[Transaction])
