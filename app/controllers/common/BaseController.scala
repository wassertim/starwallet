package controllers.common


import play.api.mvc._

import play.api.mvc.SimpleResult
import model.User
import com.codahale.jerkson.Json


class BaseController extends Controller {
  def authenticated(f: User => Request[AnyContent] => SimpleResult): EssentialAction =
    authenticated(parse.anyContent)(f)

  def authenticated[A](bp: BodyParser[A])(f: User => Request[A] => SimpleResult) = Action(bp) {
    implicit request =>
      getUser(request) match {
        case Some(id) => f(id)(request)
        case _ => Unauthorized
      }

  }

  def getUser[A](request: Request[A]): Option[User] = {
    request.session.get("identity") match {
      case Some(cookie) => Some(Json.parse[User](cookie))
      case _ => None
    }
  }
}
