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
            val sa = starbucksService.getAccountData(auth)
            Ok(Json.generate(sa))
          case _ => BadRequest("Could not find the identity")
        }
  }
}
