package service.sql

import model.{Identity, AuthInfo}
import service.sql.common.BaseService

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import org.mindrot.jbcrypt.BCrypt

class UserService extends BaseService with service.common.UserService {

  override def register[T](user: AuthInfo, onSuccess: (Int) => T, onError: (String) => T): T = database withDynSession {
    if (isValid(user)) {
      sqlu"""
      insert into users(email, password) values(${user.userName.trim.toLowerCase}, ${saltedPassword(user.password)});
    """.execute
      onSuccess(sql"select last_insert_id();".as[Int].first)
    } else {
      onError("")
    }
  }
  def authenticate(login: String, password: String) = {
    getPassword(login) match {
      case Some(hashedPassword) => {
        if (BCrypt.checkpw(password, hashedPassword))
          get(login)
        else
          Identity(0, login, isAuthenticated = false)
      }
      case None => Identity(0, login, isAuthenticated = false)
    }
  }

  def isValid(info: AuthInfo): Boolean = database withDynSession {
    sql"""
      select count(*) from users where email = ${info.userName.trim.toLowerCase}
    """.as[Int].first == 0
  }

  private def getPassword(email: String): Option[String] = database withDynSession  {
    sql"SELECT password FROM users WHERE email = ${email.trim.toLowerCase}".as[String].firstOption
  }

  def get(login: String) = database withDynSession  {
    implicit val getUserEntity = GetResult(r => Identity(r.<<, r.<<, true))
    sql"""
      SELECT
        u.id,
        u.email
      FROM
        users u
      WHERE
        u.email = ${login.trim.toLowerCase}
    """.as[Identity].first
  }

  def saltedPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())
}
