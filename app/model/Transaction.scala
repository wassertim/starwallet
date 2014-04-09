package model

import java.sql.Timestamp

case class Transaction(date: Timestamp, place: String, transactionType: String, amount: Double, balance: Double)
