package service.sql.common

import play.api.db.DB
import scala.slick.driver.JdbcDriver.simple._
import play.api.Play.current

class BaseService {
  protected lazy val database = Database.forDataSource(DB.getDataSource())
}
