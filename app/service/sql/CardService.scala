package service.sql

import service.sql.common.BaseService
import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import model._

class CardService extends BaseService with service.common.CardService {
  def list(userId: Int) = database withDynSession {
    implicit val getGardListResult = GetResult(r => Card(r.<<, r.<<, r.<<, Nil))
    sql"""
      select
        c.number,
        c.balance,
        c.is_active
      from identities i
      inner join cards c on i.id = c.identity_id
      where
        i.user_id = ${userId};
    """.as[Card].list()
  }
}
