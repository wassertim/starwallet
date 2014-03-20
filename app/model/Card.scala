package model

case class Card(number: String, balance: Double, isActive: Boolean, transactions: Seq[Transaction])
