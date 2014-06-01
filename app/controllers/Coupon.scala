package controllers

import controllers.common.BaseController
import com.google.inject.Inject
import utility.Authorized
import service.common.sql.CouponService
import play.api.libs.json.Json
import model.Coupon.writes

class Coupon @Inject()(couponService: CouponService) extends BaseController {
  def list(userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      val list = couponService.list(userId)
      Ok(Json.toJson(list))
  }

  def get(number: String, userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      couponService.get(number, userId) match {
        case Some(coupon) => Ok(Json.toJson(coupon))
        case _ => BadRequest("Could not find the coupon")
      }
  }
}
