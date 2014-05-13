package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import model._
import utility.JsonResults._
import play.api.libs.concurrent.Execution.Implicits._
import utility.{DateTimeUtility, Authorized}
import model.StarbucksAccount
import model.AuthInfo
import model.RegistrationInfo
import scala.Some
import org.joda.time.DateTime
import service.common.http._
import service.common.sql._

class Identity @Inject()(
                          identityService: IdentityService,
                          accountService: AccountService,
                          starbucks: Starbucks,
                          registrationService: RegistrationService,
                          cardService: CardService
                          ) extends BaseController {

  def add(userId: Int) = Authorized(parse.json)(Seq(userId), roles = Seq("admin")) {
    request =>
      val authInfo = Json.parse[AuthInfo](request.body.toString())
      starbucks.authenticate(authInfo).fold(
        success => {
          val id = identityService.add(authInfo, request.user.userId)
          Ok(Json.generate(id))
        },
        error =>
          BadRequest(error.errorMessage)
      )
  }

  def update(userId: Int) = Authorized(parse.json)(Seq(userId), roles = Seq("admin")) {
    request =>
      val authInfo = Json.parse[AuthInfo](request.body.toString())
      starbucks.authenticate(authInfo).fold(
        success => {
          identityService.update(authInfo, request.user.userId)
          Ok("ok")
        },
        error =>
          BadRequest(error.errorMessage)
      )
  }


  def get(id: Int, userId: Int) = Authorized(Seq(userId), roles = Seq("admin")) {
    request =>
      identityService.get(id, userId) match {
        case Some(authInfo) => Ok(Json.generate(authInfo))
        case _ => BadRequest("Could not find auth info")
      }
  }

  def remove(id: Int, userId: Int) = Authorized(Seq(userId), roles = Seq("admin")) {
    request =>
      identityService.remove(id, userId)
      Ok("ok")
  }

  def register(userId: Int) = Authorized.async(parse.json)(userIds = Seq(userId), Seq("admin")) {
    request =>
      val registrationInfo = Json.parse[RegistrationInfo](request.body.toString())
      registrationService.register(registrationInfo).map {
        result =>
          result.fold(
            authInfo => {
              val id = identityService.register(registrationInfo, userId)
              //TODO: move to register method
              cardService.savePin(registrationInfo.card)
              json(id)
            },
            error => {
              BadRequest(error)
            }
          )
      }
  }

  def list(userId: Int) = Authorized(Seq(userId), roles = Seq("admin")) {
    request =>
      json(identityService.list(userId))
  }

}
