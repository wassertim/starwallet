package controllers.common


import play.api.mvc._

import play.api.mvc.SimpleResult
import model.User
import com.codahale.jerkson.Json
import play.api.mvc.BodyParsers.parse
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

class BaseController extends Controller {

}
