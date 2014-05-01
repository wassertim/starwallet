package utility

import play.api.mvc._
import play.api.mvc.BodyParsers.parse
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import model.User
import scala.Some
import play.api.mvc.SimpleResult
import com.codahale.jerkson.Json

class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)
object Authenticated {
  def apply(f: AuthenticatedRequest[AnyContent] => SimpleResult): EssentialAction =
    apply(parse.anyContent)(f)


  def apply[A](bp: BodyParser[A])(f: AuthenticatedRequest[A] => SimpleResult) = Action(bp) {
    implicit request =>
      getUser(request) match {
        case Some(id) => f(new AuthenticatedRequest[A](id, request))
        case _ => play.api.mvc.Results.Unauthorized
      }

  }

  def async(f: AuthenticatedRequest[AnyContent] => Future[SimpleResult]): EssentialAction = {
    async(parse.anyContent)(f)
  }

  def async[A](bp: BodyParser[A])(f: AuthenticatedRequest[A] => Future[SimpleResult]) = Action.async(bp) {
    implicit request =>
      getUser(request) match {
        case Some(id) => f(new AuthenticatedRequest[A](id, request))
        case _ => Future(play.api.mvc.Results.Unauthorized)
      }

  }

  def getUser[A](request: Request[A]): Option[User] = {
    request.session.get("identity") match {
      case Some(cookie) => Some(Json.parse[User](cookie))
      case _ => None
    }
  }
}
