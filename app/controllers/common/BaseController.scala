package controllers.common


import play.api.mvc._

import play.api.mvc.SimpleResult
import model.Identity
import com.codahale.jerkson.Json


class BaseController extends Controller {
  def authenticated(f: Identity => Request[AnyContent] => SimpleResult): EssentialAction =
    authenticated(parse.anyContent)(f)

  def authenticated[A](bp: BodyParser[A])(f: Identity => Request[A] => SimpleResult) = Action(bp) {
    implicit request =>
      getIdentity(request) match {
        case Some(id) => f(id)(request)
        case _ => Unauthorized
      }

  }

  def getIdentity[A](request: Request[A]): Option[Identity] = {
    request.session.get("identity") match {
      case Some(cookie) => Some(Json.parse[Identity](cookie))
      case _ => None
    }
  }
}
