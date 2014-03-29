package service.sql

import service.sql.common.BaseService
import model.AuthInfo

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

class AccountService extends BaseService with service.common.AccountService {
  implicit val getAuthInfo = GetResult(r => AuthInfo(r.<<, r.<<, r.<<))

  def list(userId: Int): Seq[AuthInfo] = database withDynSession {
    sql"""
      select
        id,
        user_name,
        password
      from
        accounts
      where
        user_id = ${userId};
    """.as[AuthInfo].list
  }

  def add(info: AuthInfo, userId: Int): Int = database withDynSession {
    sqlu"""
      insert into
        accounts(user_name, password, user_id)
      values
        (${info.userName}, ${info.password}, ${userId});
    """.execute
    sql"select last_insert_id();".as[Int].first
  }

  def get(id: Int): Option[AuthInfo] = database withDynSession {
    sql"""
      select
        id,
        user_name,
        password
      from
        accounts
      where
        id = ${id};
    """.as[AuthInfo].firstOption
  }
}
