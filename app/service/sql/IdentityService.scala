package service.sql

import service.sql.common.BaseService
import model.AuthInfo

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import play.api.libs.Crypto

class IdentityService extends BaseService with service.common.IdentityService {


  def list(userId: Int): Seq[AuthInfo] = database withDynSession {
    implicit val getAuthInfo = GetResult(r => AuthInfo(r.<<, r.<<, Crypto.decryptAES(r.<<)))
    sql"""
      select
        id,
        user_name,
        '' as password
      from
        identities
      where
        user_id = ${userId};
    """.as[AuthInfo].list
  }

  def add(auth: AuthInfo, userId: Int): Int = database withDynSession {
    val encryptedPassword = Crypto.encryptAES(auth.password)
    sqlu"""
      insert into
        identities(user_name, password, user_id)
      values
        (${auth.userName}, ${encryptedPassword}, ${userId});
    """.execute
    Q.queryNA[Int](s"select $lastInsertId;").first()
  }

  def get(id: Int, userId: Int): Option[AuthInfo] = database withDynSession {
    implicit val getAuthInfo = GetResult(r => AuthInfo(r.<<, r.<<, Crypto.decryptAES(r.<<)))
    sql"""
      select
        id,
        user_name,
        password
      from
        identities
      where
        id = ${id} and user_id = ${userId};
    """.as[AuthInfo].firstOption
  }

  override def update(auth: AuthInfo, userId: Int): Unit = database withDynSession {
    val encryptedPassword = Crypto.encryptAES(auth.password)
    sqlu"""
      update
        identities
      set
        user_name = ${auth.userName},
        password = ${encryptedPassword}
      where
        id = ${auth.id} and user_id = ${userId};
    """.execute
  }

  override def remove(id: Int, userId: Int): Unit = database withDynSession {
    sqlu"""
      delete from identities where id = ${id} and user_id = ${userId};
    """.execute
  }

  override def encryptAllPasswords: Unit = database withDynSession {
    implicit val getRes = GetResult(r => Tuple2[AuthInfo, Int](AuthInfo(r.<<, r.<<, r.<<), r.<<))
    val unencryptedList = sql"""
      select
        id,
        user_name,
        password,
        user_id
      from
        identities
    """.as[Tuple2[AuthInfo, Int]].list
    unencryptedList.foreach {
      listItem =>
        val (auth, userId) = listItem
        update(auth, userId)
    }
  }
}
