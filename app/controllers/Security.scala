package controllers

import play.api.mvc.{Action, BodyParsers, Controller}
import model._
import Login.reads, User.writes
import com.google.inject.Inject
import service.common.sql.UserService
import play.api.libs.json.Json
import utility.JsonResults._
import scala.Some

class Security @Inject()(userService: UserService) extends Controller {
  def signUp = Action(BodyParsers.parse.json) {
    request =>
      val user = request.body.as[Login]
      userService.register(user, userId => {
        val identity = User(userId, user.userName, isAuthenticated = true)
        val jsIdentity = Json.toJson(identity)
        Ok(jsIdentity).withSession("identity" -> jsIdentity.toString)
      }, errorMessage => {
        BadRequest(errorMessage)
      })
  }

  def checkAuth = Action {
    request =>
      request.session.get("identity") match {
        case Some(cookie) => Ok(cookie)
        case _ => Ok(Json.toJson(User(0, "", isAuthenticated = false)))
      }

  }

  def signIn = Action(BodyParsers.parse.json) {
    request =>
      val user = request.body.as[Login]
      val identity = userService.authenticate(user)
      val result = json(identity)
      if (identity.isAuthenticated)
        result.withSession("identity" -> Json.toJson(identity).toString())
      else
        result

  }

  def signOut = Action {
    Ok("").withNewSession
  }

}
