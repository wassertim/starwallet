package controllers

import com.google.inject.Inject
import play.api.mvc.{BodyParsers, Action}
import service.common._
import com.codahale.jerkson.Json
import model._
import controllers.common.BaseController
import utility.{Authorized, JsonResults, Authenticated}
import JsonResults._
import service.common.sql.UserService

class User @Inject()(val userService: UserService) extends BaseController {

  def getSettings(userId: Int) = Authorized(Seq(userId), roles = Seq("admin")) {
    request =>
      userService.getSettings(userId) match {
        case Some(settings) =>
          Ok(Json.generate(settings))
        case _ => BadRequest("User settings does not exist")
      }
  }

  def saveSettings(userId: Int) = Authorized(parse.json)(Seq(userId), roles = Seq("admin")) {
    request =>
      val settings = Json.parse[UserSettings](request.body.toString())
      userService.saveSettings(settings, userId)
      Ok("ok")
  }





}
