package controllers

import play.api.mvc._
import com.google.inject.Inject
import play.api.{Play, Routes}
import service.common.http.Starbucks
import utility.Authenticated
import Play.{current => p}

class Application @Inject()(val starbucks: Starbucks) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def javascriptRoutes = Action {
    implicit request =>
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          routes.javascript.User.signUp,
          routes.javascript.User.checkAuth,
          routes.javascript.User.signIn,
          routes.javascript.User.signOut,
          routes.javascript.User.saveSettings,
          routes.javascript.User.getSettings,
          routes.javascript.Identity.add,
          routes.javascript.Identity.get,
          routes.javascript.Identity.list,
          routes.javascript.Identity.update,
          routes.javascript.Identity.remove,
          routes.javascript.Identity.register,
          routes.javascript.Account.get,
          routes.javascript.Account.activate,
          routes.javascript.Card.list,
          routes.javascript.Card.get,
          routes.javascript.Card.savePin,
          routes.javascript.Coupon.list,
          routes.javascript.Coupon.get,
          routes.javascript.BarCode.cardBarCode
        )
      ).as("text/javascript")
  }

  def activationEmail = Authenticated {
    r =>
      Ok(p.configuration.getString("activation.email").get)
  }
}