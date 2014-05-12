package service.prod

import service.prod.common.BaseService
import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import model._

class CouponService extends BaseService with service.common.CouponService {
  def list(userId: Int): Seq[Coupon] = database withDynSession {
    implicit val getCouponListResult = GetResult(r => Coupon(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        c.number,
        c.issue_date,
        c.expiration_date,
        c.is_active,
        c.type,
        c.url_key
      from
        identities i
      inner join coupons c on c.identity_id = i.id
      where
        i.user_id = ${userId} and c.is_active = true order by c.expiration_date asc;
    """.as[Coupon].list
  }

  override def get(number: String, userId: Int): Option[Coupon] = database withDynSession {
    implicit val getCouponListResult = GetResult(r => Coupon(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        c.number,
        c.issue_date,
        c.expiration_date,
        c.is_active,
        c.type,
        c.url_key
      from
        identities i
      inner join coupons c on c.identity_id = i.id
      where
        c.number = ${number} and i.user_id = ${userId};
    """.as[Coupon].firstOption
  }
}
