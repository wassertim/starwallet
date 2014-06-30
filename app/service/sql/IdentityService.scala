package service.sql

import service.sql.common.BaseService
import model._

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import play.api.libs.Crypto

class IdentityService extends BaseService with service.common.sql.IdentityService {

  def list(userId: Int) = database withDynSession {
    implicit val getAuthInfo = GetResult(r => IdentityListItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        i.id,
        i.user_name,
        a.stars_count,
        (select count(*) from coupons where identity_id = i.id and is_active = true) as coupons_count,
        a.sync_date,
        i.is_active
      from
        identities i
      left join accounts a on a.identity_id = i.id
      where
        user_id = ${userId};
    """.as[IdentityListItem].list
  }



  def register(info: RegistrationInfo, userId: Int): Int = database withDynSession {
    val encryptedPassword = Crypto.encryptAES(info.auth.password)
    sqlu"""
      insert into
        identities(user_name, password, user_id, activation_email)
      values
        (${info.auth.userName}, ${encryptedPassword}, ${userId}, ${info.email});
    """.execute
    Q.queryNA[Int](s"select $lastInsertId;").first()
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
    implicit val getAuthInfo = GetResult(r => AuthInfo(r.<<, r.<<, Crypto.decryptAES(r.<<), r.<<, r.<<))
    sql"""
      select
        id,
        user_name,
        password,
        activation_email,
        is_active
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
    sqlu"delete from transactions where card_number in (select number from cards where identity_id = ${id});".execute
    sqlu"delete from pin_codes where card_number in (select number from cards where identity_id = ${id});".execute
    sqlu"delete from cards where identity_id = ${id};".execute
    sqlu"delete from coupons where identity_id = ${id};".execute
    sqlu"delete from accounts where identity_id = ${id}".execute
    sqlu"delete from identities where id = ${id} and user_id = ${userId};".execute
  }

  override def encryptAllPasswords: Unit = database withDynSession {
    implicit val getRes = GetResult(r => Tuple2[AuthInfo, Int](AuthInfo(r.<<, r.<<, r.<<, None, false), r.<<))
    val unencryptedList = sql"""
      select
        id,
        user_name,
        password,
        user_id
      from
        identities
    """.as[(AuthInfo, Int)].list
    unencryptedList.foreach {
      listItem =>
        val (auth, userId) = listItem
        update(auth, userId)
    }
  }

  override def getOwnerId(identityId: Int): Int = database withDynSession {
    sql"select user_id from identities where id = ${identityId}".as[Int].first
  }

  def listAuth(userId: Int) = database withDynSession {
    implicit val getAuthInfo = GetResult(r => AuthInfo(r.<<, r.<<, Crypto.decryptAES(r.<<), r.<<, r.<<))
    sql"""
      select
        id,
        user_name,
        password,
        activation_email,
        is_active
      from
        identities
      where
        user_id = ${userId};
    """.as[AuthInfo].list
  }
}
