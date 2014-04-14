package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json

class Card @Inject()(cardService: service.common.CardService) extends BaseController {
  def list(userId: Int) = authenticated {
    identity =>
      request =>
        if (identity.userId == userId) {
          val list = cardService.listByUser(userId)
          Ok(Json.generate(list))
        } else {
          Unauthorized("You are not authorized to get this card list")
        }
  }

  def get(number: String, userId: Int) = authenticated {
    user =>
      request =>
        if (userId != user.userId) {
          BadRequest("You are not authorized for viewing the card")
        } else {
          cardService.get(number, userId) match {
            case Some(card) => Ok(Json.generate(card))
            case _ => BadRequest("Could not find the card")
          }
        }

  }
}
