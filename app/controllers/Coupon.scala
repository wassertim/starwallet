package controllers

import controllers.common.BaseController
import com.google.inject.Inject
import com.codahale.jerkson.Json

class Coupon @Inject()(couponService: service.common.CouponService) extends BaseController {
  def list(userId: Int) = authenticated {
    user =>
      request =>
        val list = couponService.list(userId)
        Ok(Json.generate(list))
  }

  def get(number: String, userId: Int) = authenticated {
    user =>
      request =>
        if (user.userId != userId) {
          BadRequest("You are not authorized to view the coupon")
        } else {
          couponService.get(number, userId) match {
            case Some(coupon) => Ok(Json.generate(coupon))
            case _ => BadRequest("Could not find the coupon")
          }
        }
  }
}
