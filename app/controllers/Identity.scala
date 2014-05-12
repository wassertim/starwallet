package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import model._
import play.api.libs.concurrent.Execution.Implicits._
import utility.JsonResults._
import utility.{DateTimeUtility, Authenticated, Authorized}
import scala.concurrent.Future
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
  cardService: CardService,
  emailClient: service.common.pop.EmailClient
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

  def activate(id: Int, userId: Int) = Authenticated.async {
    request =>
      accountService.get(id, userId) match {
        case Some(account) =>
          Future {
            val activationUrl = emailClient.getActivationUrl(account.email.get)
            registrationService.activate(activationUrl)
            Ok("ok")
          }
      }

  }

  def register(userId: Int) = Authorized.async(parse.json)(userIds = Seq(userId), Seq("admin")) {
    request =>
      val acc = Json.parse[RegistrationInfo](request.body.toString())
      registrationService.register(acc).map {
        result =>
          result.fold(
            authInfo => {
              val id = identityService.add(acc.auth, userId)
              cardService.savePin(acc.card)
              //syncAccount(acc, id)
              json(id)
            },
            error => {
              BadRequest(error)
            }
          )
      }
  }

  def syncAccount(acc: RegistrationInfo, id: Int) {
    val account = StarbucksAccount(
      acc.auth.userName,
      0,
      List(Card(acc.card, 0, isActive = true, Nil)),
      Nil,
      DateTimeUtility.ts(DateTime.now),
      Some(acc.email)
    )
    accountService.sync(account, id)
  }

  def list(userId: Int) = Authorized(Seq(userId), roles = Seq("admin")) {
    request =>
      json(identityService.list(userId))
  }

}
