package model

case class StarbucksAccount(
  userName: String,
  starsCount: Int,
  cards: Seq[Card],
  couponList: Seq[Coupon]
)
