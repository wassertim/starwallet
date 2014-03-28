package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import model.AuthInfo

class Account @Inject()(accountService: service.common.AccountService) extends BaseController {
  def add = withUser(parse.json) {
    identity =>
      request =>
        val authInfo = Json.parse[AuthInfo](request.body.toString())
        val id = accountService.add(authInfo)
        Ok(Json.generate(id))
  }
}
