package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import model.AuthInfo

class Identity @Inject()(accountService: service.common.IdentityService, starbucks: service.common.Starbucks) extends BaseController {
  def add(userId: Int) = authenticated(parse.json) {
    identity =>
      request =>
        val authInfo = Json.parse[AuthInfo](request.body.toString())
        starbucks.authenticate(authInfo).fold(
          page => {
            val id = accountService.add(authInfo, identity.userId)
            Ok(Json.generate(id))
          },
          error =>
            BadRequest(error.errorMessage)
        )
  }

  def update(userId: Int) = authenticated(parse.json) {
    identity =>
      request =>
        val authInfo = Json.parse[AuthInfo](request.body.toString())
        accountService.update(authInfo, identity.userId)
        Ok("ok")
  }

  def get(id: Int, userId: Int) = authenticated {
    identity =>
      request =>
        accountService.get(id, identity.userId) match {
          case Some(authInfo) => Ok(Json.generate(authInfo))
          case _ => BadRequest("Could not find auth info")
        }

  }

  def remove(id: Int, userId: Int) = authenticated {
    identity =>
      request =>
        accountService.remove(id, identity.userId)
        Ok("ok")
  }
  def list(userId: Int) = authenticated {
    identity =>
      request =>
        if (identity.userId == userId)
          Ok(Json.generate(accountService.list(userId)))
        else
          BadRequest("You are not authorized to view the accounts of the user.")
  }

}
