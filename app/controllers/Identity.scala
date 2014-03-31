package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import model.AuthInfo

class Identity @Inject()(accountService: service.common.IdentityService) extends BaseController {
  def add(userId: Int) = authenticated(parse.json) {
    identity =>
      request =>
        val authInfo = Json.parse[AuthInfo](request.body.toString())
        val id = accountService.add(authInfo, userId)
        Ok(Json.generate(id))
  }

  def get(id: Int, userId: Int) = authenticated {
    identity =>
      request =>
        accountService.get(id) match {
          case Some(authInfo) => Ok(Json.generate(authInfo))
          case _ => BadRequest("Could not find auth info")
        }

  }

  def list(userId: Int) = authenticated {
    identity =>
      request =>
        Ok(Json.generate(accountService.list(userId)))
  }
}
