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

object Authorized {
  def isValid(userIds: Seq[Int], roles: Seq[String], user: User) = (userIds.isEmpty || userIds.contains(user.userId)) || roles.contains(user.role)

  def apply(userIds: Seq[Int] = Nil, roles: Seq[String] = Nil)(f: AuthenticatedRequest[AnyContent] => SimpleResult): EssentialAction =
    apply(parse.anyContent)(userIds, roles)(f)

  def apply[A](bp: BodyParser[A])(userIds: Seq[Int] = Nil, roles: Seq[String] = Nil)(f: AuthenticatedRequest[A] => SimpleResult) =
    Authenticated(bp) {
      request =>
        if (isValid(userIds, roles, request.user)) {
          f(request)
        } else {
          Results.Unauthorized("You are not authorized for this request")
        }
    }

  def async[A](bp: BodyParser[A])(userIds: Seq[Int] = Nil, roles: Seq[String] = Nil)(f: AuthenticatedRequest[A] => Future[SimpleResult]) =
    Authenticated.async(bp) {
      request =>
        if (isValid(userIds, roles, request.user)) {
          f(request)
        } else {
          Future(Results.Unauthorized("You are not authorized for this request"))
        }
    }

  def async(userIds: Seq[Int] = Nil, roles: Seq[String] = Nil)(f: AuthenticatedRequest[AnyContent] => Future[SimpleResult]): EssentialAction =
    async(parse.anyContent)(userIds, roles)(f)
}

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

  def apply(f: => SimpleResult): EssentialAction = apply(_ => f)

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
