package service.sql

import service.sql.common.BaseService
import model.AuthInfo

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

class AccountService extends BaseService with service.common.AccountService {
  override def getAuthInfo(accountLogin: String, userId: Int): AuthInfo = ???

  override def list(userId: Int): Seq[AuthInfo] = database withDynSession {
    implicit val getAuthInfo = GetResult(r => AuthInfo(r.<<, r.<<))
    sql"""
      select
        user_name,
        password
      from
        accounts
      where
        user_id = ${userId};
    """.as[AuthInfo].list
  }

  override def add(info: AuthInfo): Int = database withDynSession {
    sqlu"""
      insert into
        accounts(user_name, password)
      values
        (${info.userName}, ${info.password});
    """.execute
    sql"select last_insert_id();".as[Int].first
  }
}
