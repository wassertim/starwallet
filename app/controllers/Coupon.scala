package controllers

import controllers.common.BaseController
import com.google.inject.Inject
import com.codahale.jerkson.Json
import utility.Authenticated

class Coupon @Inject()(couponService: service.common.CouponService) extends BaseController {
  def list(userId: Int) = Authenticated {
    request =>
      val list = couponService.list(userId)
      Ok(Json.generate(list))
  }

  def get(number: String, userId: Int) = Authenticated {
    request =>
      if (request.user.userId != userId) {
        BadRequest("You are not authorized to view the coupon")
      } else {
        couponService.get(number, userId) match {
          case Some(coupon) => Ok(Json.generate(coupon))
          case _ => BadRequest("Could not find the coupon")
        }
      }
  }
}
