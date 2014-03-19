package controllers

import play.api.mvc._
import com.google.inject.Inject

class Application @Inject()(val starbucks: service.common.Starbucks) extends Controller {

  def index = Action {
    val starbucksAccount = starbucks.auth("test", "test")
    Ok(views.html.index("Your new application is ready."))
  }
}