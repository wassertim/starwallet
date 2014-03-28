package controllers

import com.google.inject.Inject
import play.api.mvc.{BodyParsers, Action, Controller}
import service.common._
import com.codahale.jerkson.Json
import model._

class User @Inject()(val userService: UserService) extends Controller {
  def checkAuth = Action {
    request =>
      request.session.get("identity") match {
        case Some(cookie) => Ok(cookie)
        case _ => Ok(Json.generate(Identity(0, "", false)))
      }

  }

  def signIn = Action(BodyParsers.parse.json) {
    request =>
      val user = Json.parse[AuthInfo](request.body.toString())
      val auth = userService.authenticate(user)
      val ok = Ok(Json.generate(auth))
      if (auth.isAuthenticated)
        ok.withSession("identity" -> Json.generate(auth))
      else
        ok

  }

  def signOut = Action {
    request =>
      Ok("").withNewSession
  }
  def signUp = Action(BodyParsers.parse.json) {
    request =>
      val user = Json.parse[AuthInfo](request.body.toString())
      userService.register(user, userId => {
        val identity = Identity(userId, user.userName, true)
        Ok(Json.generate(identity)).withSession("identity" -> Json.generate(identity))
      }, errorMessage => {
        BadRequest(errorMessage)
      })
  }

}
