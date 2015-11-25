package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import play.api.libs.concurrent.Execution.Implicits._
import utility.{Authenticated, Authorized}
import play.api.mvc.{Action, SimpleResult}
import service.common.http.Starbucks
import service.common.sql.{IdentityService, AccountService}
import scala.concurrent.Future
import play.api.libs.json.Json

class Account @Inject()(
                         identityService: IdentityService,
                         starbucksService: Starbucks,
                         accountService: AccountService,
                         emailClient: service.common.pop.EmailClient) extends BaseController {

  private def identityOwnerId(id: Int) = identityService.getOwnerId(id)

  def get(id: Int, resync: Boolean) = Action {
    request =>
      getAccount(resync, id, 2)
  }

  def activate(id: Int, userId: Int) = Authenticated.async {
    request =>
      identityService.get(id) match {
        case Some(account) =>
          account.activationEmail match {
            case Some(email) => {
              val activationUrl = emailClient.getActivationUrl(email)
              starbucksService.activate(activationUrl) map {
                e =>
                  e.fold(
                    ok => Ok("ok"),
                    error => BadRequest(error)
                  )
              }
            }
            case None => {
              Future(BadRequest("This account can not be activated automatically"))
            }
          }
      }

  }

  def refreshAll = Authenticated {
    request =>
      identityService.listAuth(request.user.userId).map {
        auth =>
          starbucksService.getAccount(auth) match {
            case Some(starbucksAccount) =>
              accountService.sync(starbucksAccount, auth.id)
          }
      }
      Ok("")
  }
  def getAccount(resync: Boolean, accountId: Int, recursionCounter: Int): SimpleResult = resync match {
    case true => identityService.get(accountId) match {
      case Some(auth) => starbucksService.getAccount(auth) match {
        case Some(starbucksAccount) =>
          accountService.sync(starbucksAccount, auth.id)
          getAccount(resync = false, accountId, recursionCounter - 1)
        case _ => BadRequest(Json.toJson(auth))
      }
      case _ => BadRequest("Could not find the identity")
    }
    case false => accountService.get(accountId) match {
      case Some(cachedAccount) => Ok(Json.toJson(cachedAccount))
      case _ =>
        if (recursionCounter < 1) BadRequest("Could not find the account")
        else getAccount(resync = true, accountId, recursionCounter - 1)
    }
  }


}
