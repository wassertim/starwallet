package controllers

import play.api.mvc._
import com.google.inject.Inject
import model.AuthInfo

class Application @Inject()(val starbucks: service.common.Starbucks) extends Controller {

  def index = Action {
    val account = starbucks.getAccountData(AuthInfo("test", "test"))
    Ok(views.html.index("Your new application is ready."))
  }
}