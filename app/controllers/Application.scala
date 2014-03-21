package controllers

import play.api.mvc._
import com.google.inject.Inject

class Application @Inject()(val starbucks: service.common.Starbucks) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
}