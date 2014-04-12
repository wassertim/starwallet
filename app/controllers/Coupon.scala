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
}
