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
    //replace is only needed for staged play deployment
    val pathToApp = play.Play.application().path().getAbsolutePath.replace("target/universal/stage", "")
    val file = if (Play.isProd) {
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
      import routes.javascript._
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          Security.signUp,
          Security.checkAuth,
          Security.signIn,
          Security.signOut,
          Account.refreshAll,
          Account.get,
          Account.activate,
          BarCode.cardBarCode,
          User.saveSettings,
          User.getSettings,
          Identity.add,
          Identity.get,
          Identity.list,
          Identity.update,
          Identity.remove,
          Identity.register,
          Card.list,
          Card.get,
          Card.savePin,
          Coupon.list,
          Coupon.get
        )
      ).as("text/javascript")
  }

  def activationEmail = Authenticated {
    r => Ok(p.configuration.getString("activation.email").get)
  }
}