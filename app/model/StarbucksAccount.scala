package model

import java.sql.Timestamp

case class StarbucksAccount(
  userName: String,
  starsCount: Int,
  cards: Seq[Card],
  coupons: Seq[Coupon],
  syncDate: Timestamp
)
