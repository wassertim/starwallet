package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json

class Card @Inject()(cardService: service.common.CardService) extends BaseController {
  def list(userId: Int) = authenticated {
    identity =>
      request =>
        if (identity.userId == userId) {
          val list = cardService.list(userId)
          Ok(Json.generate(list))
        } else {
          Unauthorized("You are not authorized to get this card list")
        }
  }
}
