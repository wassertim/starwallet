package model

case class StarbucksAccount(
  cardNumber: String,
  starsCount: Int,
  cardBalance: Double,
  cardStatus: String,
  couponList: Seq[Coupon],
  transactionList: Seq[Transaction]
)
