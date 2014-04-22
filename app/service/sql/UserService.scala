package service.sql

import model._
import service.sql.common.BaseService

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import org.mindrot.jbcrypt.BCrypt

class UserService extends BaseService with service.common.UserService {

  def saltedPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

  override def register[T](user: Login, onSuccess: (Int) => T, onError: (String) => T): T = database withDynSession {
    if (isValid(user)) {
      sqlu"""
      insert into users(email, password) values(${user.userName.trim.toLowerCase}, ${saltedPassword(user.password)});
    """.execute
      onSuccess(Q.queryNA[Int](s"select $lastInsertId;").first())
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
          User(0, login, isAuthenticated = false)
      }
      case None => User(0, login, isAuthenticated = false)
    }
  }

  def isValid(info: Login): Boolean = database withDynSession {
    sql"""
      select count(*) from users where email = ${info.userName.trim.toLowerCase}
    """.as[Int].first == 0
  }

  private def getPassword(email: String): Option[String] = database withDynSession {
    sql"SELECT password FROM users WHERE email = ${email.trim.toLowerCase}".as[String].firstOption
  }

  def get(login: String) = database withDynSession {
    implicit val getUserEntity = GetResult(r => User(r.<<, r.<<, true))
    sql"""
      SELECT
        u.id,
        u.email
      FROM
        users u
      WHERE
        u.email = ${login.trim.toLowerCase}
    """.as[User].first
  }

  def getSettings(userId: Int) = database withDynSession {
    implicit val getResults = GetResult(r => UserSettings(r.<<, r.<<, r.<<, r.<<))
    sql"""
      SELECT
        first_name,
        last_name,
        phone,
        email_domain
      from
        user_settings
      where
        user_id = ${userId};
    """.as[UserSettings].firstOption
  }

  def saveSettings(settings: UserSettings, userId: Int) = database withDynSession {
    sqlu"delete from user_settings where user_id = ${userId};".execute
    sqlu"""
      insert into user_settings(user_id, first_name, last_name, phone, email_domain)
      values(${userId}, ${settings.firstName}, ${settings.lastName}, ${settings.phone}, ${settings.emailDomain});
    """.execute
  }
}
