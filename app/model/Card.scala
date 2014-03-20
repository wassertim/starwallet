package model

case class Card(number: String, balance: Double, status: String, transactions: Seq[Transaction])
