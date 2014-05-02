package controllers

import com.google.inject.Inject
import play.api.mvc.{BodyParsers, Action}
import service.common._
import com.codahale.jerkson.Json
import model._
import controllers.common.BaseController
import utility.{Authorized, JsonResults, Authenticated}
import JsonResults._

class User @Inject()(val userService: UserService) extends BaseController {
  def checkAuth = Action {
    request =>
      request.session.get("identity") match {
        case Some(cookie) => Ok(cookie)
        case _ => Ok(Json.generate(User(0, "", false)))
      }

  }

  def signIn = Action(BodyParsers.parse.json) {
    request =>
      val user = Json.parse[Login](request.body.toString())
      val identity = userService.authenticate(user)
      val result = json(identity)
      if (identity.isAuthenticated)
        result.withSession("identity" -> Json.generate(identity))
      else
        result

  }

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

  def signOut = Action {
      Ok("").withNewSession
  }

  def signUp = Action(BodyParsers.parse.json) {
    request =>
      val user = Json.parse[Login](request.body.toString())
      userService.register(user, userId => {
        val identity = User(userId, user.userName, isAuthenticated = true)
        json(identity).withSession("identity" -> Json.generate(identity))
      }, errorMessage => {
        BadRequest(errorMessage)
      })
  }

}
