package controllers

import play.api.mvc._
import com.google.inject.Inject
import play.api.{Play, Routes}
import service.common.http.Starbucks
import utility.Authenticated
import Play.{current => p}
import java.io.File

class Application @Inject()(val starbucks: Starbucks) extends Controller {

  def index = Action {
    val pathToApp = play.Play.application().path().getAbsolutePath
    val file = if (!Play.isProd) {
      "public/dist/index.html"
    } else {
      "index.html"
    }
    Ok.sendFile(
      content = new File(s"$pathToApp/$file"),
      fileName = _ => "index.html",
      inline = true
    )
  }

  def javascriptRoutes = Action {
    implicit request =>
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          routes.javascript.Security.signUp,
          routes.javascript.Security.checkAuth,
          routes.javascript.Security.signIn,
          routes.javascript.Security.signOut,
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