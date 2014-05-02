package controllers

import controllers.common.BaseController
import com.google.inject.Inject
import utility.Authorized
import utility.JsonResults._

class Coupon @Inject()(couponService: service.common.CouponService) extends BaseController {
  def list(userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      val list = couponService.list(userId)
      json(list)
  }

  def get(number: String, userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      couponService.get(number, userId) match {
        case Some(coupon) => json(coupon)
        case _ => BadRequest("Could not find the coupon")
      }
  }
}
