package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import service.common._

class Account @Inject()(
      identityService: IdentityService,
      starbucksService: Starbucks,
      accountService: AccountService) extends BaseController {

  def get(id: Int) = authenticated {
    identity =>
      request =>
        getAccount(id, identity.userId)
  }

  private def getAccount(id: Int, userId: Int) = {
    accountService.get(id) match {
      case Some(starbucksAccount) => {
        Ok(Json.generate(starbucksAccount))
      }
      case _ => identityService.get(id, userId) match {
        case Some(auth) => starbucksService.getAccount(auth) match {
          case Some(starbucksAccount) => {
            accountService.sync(starbucksAccount, id)
            Ok(Json.generate(starbucksAccount))
          }
          case _ => BadRequest("Could not load the data from Starbucks")
        }
        case _ => BadRequest("Could not find the identity")
      }
    }
  }
}
