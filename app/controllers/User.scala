package controllers

import com.google.inject.Inject
import play.api.mvc.{BodyParsers, Action, Controller}
import service.common._
import com.codahale.jerkson.Json
import model._

class User @Inject()(val userService: UserService) extends Controller {
  def checkAuth = Action {
    Ok(Json.generate(Identity(0, "", false)))
  }
  def signIn = Action(BodyParsers.parse.json) {
    request =>
      val user = Json.parse[AuthInfo](request.body.toString())
      val auth = userService.authenticate(user)
      Ok(Json.generate(auth))
  }
  def signUp = Action(BodyParsers.parse.json) {
    request =>
      val user = Json.parse[AuthInfo](request.body.toString())
      userService.register(user, userId => {
        authenticate()
        Ok(Json.generate(Identity(userId, user.userName, true)))
      },
      errorMessage => {
        BadRequest(errorMessage)
      })
  }

  def authenticate() = {}
}
