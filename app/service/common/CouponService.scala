package service.common

import model.Coupon

trait CouponService {
  def get(number: String, userId: Int): Option[Coupon]

  def list(userId: Int): Seq[Coupon]
}
