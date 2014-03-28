package controllers

import play.api.mvc._
import com.google.inject.Inject
import scala.reflect.io.File
import play.api.Routes

class Application @Inject()(val starbucks: service.common.Starbucks) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def javascriptRoutes = Action {
    implicit request =>
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          routes.javascript.User.signUp,
          routes.javascript.User.checkAuth,
          routes.javascript.User.signIn
        )
      ).as("text/javascript")
  }
}