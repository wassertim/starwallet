package service.sql

import service.sql.common.BaseService
import model.AuthInfo

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

class IdentityService extends BaseService with service.common.IdentityService {
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
    Q.queryNA[Int](s"select $lastInsertId;").first()
  }

  def get(id: Int, userId: Int): Option[AuthInfo] = database withDynSession {
    sql"""
      select
        id,
        user_name,
        password
      from
        accounts
      where
        id = ${id} and user_id = ${userId};
    """.as[AuthInfo].firstOption
  }

  override def update(auth: AuthInfo, userId: Int): Unit = database withDynSession {
    sqlu"""
      update
        accounts
      set
        user_name = ${auth.userName},
        password = ${auth.password}
      where
        id = ${auth.id} and user_id = ${userId};
    """.execute
  }

  override def remove(id: Int, userId: Int): Unit = database withDynSession {
    sqlu"""
      delete from accounts where id = ${id} and user_id = ${userId};
    """.execute
  }
}
