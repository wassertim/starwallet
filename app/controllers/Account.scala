package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import service.common._

class Account @Inject()(
      identityService: IdentityService,
      starbucksService: Starbucks,
      accountService: AccountService) extends BaseController {

  def get(id: Int, resync: Boolean) = authenticated {
    identity =>
      request =>
        getAccount(id, identity.userId, resync)
  }

  private def getAccount(id: Int, userId: Int, resync: Boolean) = {
    if (resync)
      refresh(id, userId)
    else {
      accountService.get(id) match {
        case Some(cachedAccount) => Ok(Json.generate(cachedAccount))
        case _ => refresh(id, userId)
      }
    }
  }

  private def refresh(id: Int, userId: Int) = identityService.get(id, userId) match {
    case Some(auth) => starbucksService.getAccount(auth) match {
      case Some(starbucksAccount) => {
        accountService.sync(starbucksAccount, id)
        accountService.get(id) match {
          case Some(cachedAccount) => Ok(Json.generate(cachedAccount))
          case _ => BadRequest("Could not load the data from Starbucks")
        }
      }
      case _ => BadRequest("Could not load the data from Starbucks")
    }
    case _ => BadRequest("Could not find the identity")
  }
}
