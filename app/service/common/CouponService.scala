package service.common

import model.Coupon

trait CouponService {
  def list(userId: Int): Seq[Coupon]
}
