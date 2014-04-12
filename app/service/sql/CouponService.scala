package service.sql

import service.sql.common.BaseService
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
        c.is_active = 1 order by c.expiration_date asc;
    """.as[Coupon].list
  }
}
