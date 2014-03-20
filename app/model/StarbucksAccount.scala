package model

case class StarbucksAccount(
  userName: String,
  starsCount: Int,
  cards: Seq[Card],
  coupons: Seq[Coupon]
)
