package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import model.{RegistrationInfo, AuthInfo}
import play.api.libs.concurrent.Execution.Implicits._
import utility.JsonResults._
import utility.Authorized

class Identity @Inject()(
  identityService: service.common.IdentityService,
  starbucks: service.common.Starbucks,
  registrationService: service.common.RegistrationService,
  cardService: service.common.CardService
  ) extends BaseController {

  def add(userId: Int) = Authorized(parse.json)(Seq(userId), roles = Seq("admin")) {
    request =>
      val authInfo = Json.parse[AuthInfo](request.body.toString())
      starbucks.authenticate(authInfo).fold(
        page => {
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

  def register(userId: Int) = Authorized.async(parse.json)(Seq(userId), roles = Seq("admin")) {
    request =>
      val acc = Json.parse[RegistrationInfo](request.body.toString())
      registrationService.register(acc).map {
        result =>
          result.fold(
            authInfo => {
              val id = identityService.add(acc.auth, userId)
              cardService.savePin(acc.card)
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
