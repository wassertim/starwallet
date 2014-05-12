package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import service.common._
import utility.Authorized
import utility.JsonResults._
import play.api.mvc.SimpleResult
import service.common.http.Starbucks
import service.common.sql.{IdentityService, AccountService}

class Account @Inject()(
  identityService: IdentityService,
  starbucksService: Starbucks,
  accountService: AccountService) extends BaseController {

  private def identityOwnerId(id: Int) = identityService.getOwnerId(id)

  def get(id: Int, resync: Boolean) = Authorized(userIds = Seq(identityOwnerId(id)), roles = Seq("admin")) {
    request =>
      getAccount(resync, id, request.user.userId, 2)
  }


  def getAccount(resync: Boolean, id: Int, userId: Int, recursionCounter: Int): SimpleResult = resync match {
    case true => {
      identityService.get(id, userId) match {
        case Some(auth) => starbucksService.getAccount(auth) match {
          case Some(starbucksAccount) =>
            accountService.sync(starbucksAccount, id)
            getAccount(resync = false, id, userId, recursionCounter - 1)
        }
      }
    }
    case false => {
      accountService.get(id, userId) match {
        case Some(cachedAccount) => json(cachedAccount)
        case _ =>
          if (recursionCounter > 1) BadRequest("Could not find the account")
          else getAccount(resync = true, id, userId, recursionCounter - 1)
      }
    }
  }


}
