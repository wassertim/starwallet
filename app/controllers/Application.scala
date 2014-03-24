package controllers

import play.api.mvc._
import com.google.inject.Inject
import scala.reflect.io.File

class Application @Inject()(val starbucks: service.common.Starbucks) extends Controller {

  def index = Action {
    val e = ""
    Ok(views.html.index("Your new application is ready."))
  }
}