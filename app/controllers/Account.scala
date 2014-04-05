package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json

class Account @Inject()(identityService: service.common.IdentityService, starbucksService: service.common.Starbucks) extends BaseController {
  def getByIdentityId(id: Int) = authenticated {
    identity =>
      request =>
        identityService.get(id, identity.userId) match {
          case Some(auth) =>
            starbucksService.getAccount(auth) match {
              case Some(starbucksAccount) => Ok(Json.generate(starbucksAccount))
              case _ => BadRequest("Could not load the data from Starbucks")
            }
          case _ => BadRequest("Could not find the identity")
        }
  }
}
