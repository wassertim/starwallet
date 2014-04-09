package service.sql.common

import play.api.db.DB
import scala.slick.driver.JdbcDriver.simple._
import play.api.Play.current
import play.api.Play


class BaseService {
  protected lazy val database = Database.forDataSource(DB.getDataSource(Play.current.configuration.getString("database.current").getOrElse("")))

  val lastInsertId = Play.current.configuration.getString("database.current") match {
    case Some(x) => if (x == "postgres") "lastval()" else "last_insert_id()"
    case _ => "last_insert_id()"
  }

}
