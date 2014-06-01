package controllers

import com.google.inject.Inject
import model._
import controllers.common.BaseController
import utility.Authorized
import service.common.sql.UserService
import play.api.libs.json.Json

class User @Inject()(val userService: UserService) extends BaseController {

  def getSettings(userId: Int) = Authorized(Seq(userId), roles = Seq("admin")) {
    request =>
      userService.getSettings(userId) match {
        case Some(settings) =>
          Ok(Json.toJson(settings))
        case _ => BadRequest("User settings does not exist")
      }
  }

  def saveSettings(userId: Int) = Authorized(parse.json)(Seq(userId), roles = Seq("admin")) {
    request =>
      val settings = request.body.as[UserSettings]
      userService.saveSettings(settings, userId)
      Ok("ok")
  }





}
