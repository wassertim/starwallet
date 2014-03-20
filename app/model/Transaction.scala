package model

import org.joda.time.DateTime

case class Transaction(date: DateTime, store: String, transactionType: String, subAmount: Double, balance: Double)
