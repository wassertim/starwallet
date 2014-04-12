package service.sql

import service.sql.common.BaseService

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import model._

class CardService extends BaseService with service.common.CardService {
  def list(userId: Int) = database withDynSession {
    implicit val getGardListResult = GetResult(r => CardListItem(r.<<, r.<<, r.<<, r.<<, r.<<, AuthInfo(r.<<, r.<<, "")))
    sql"""
      select
        c.number,
        c.balance,
        c.is_active,
        c.last_transaction_date,
        c.activation_date,
        i.id,
        i.user_name
      from identities i
      inner join cards c on i.id = c.identity_id
      where
        i.user_id = ${userId}
      order by c.last_transaction_date desc;
    """.as[CardListItem].list()
  }
}
