package model

import java.sql.Timestamp

case class StarbucksAccount[T](
  userName: String,
  starsCount: Int,
  cards: Seq[T],
  coupons: Seq[Coupon],
  syncDate: Timestamp
)
