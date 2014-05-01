package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import com.codahale.jerkson.Json
import utility.Authenticated

class Card @Inject()(cardService: service.common.CardService) extends BaseController {
  def list(userId: Int) = Authenticated {
    request =>
      if (request.user.userId == userId) {
        val list = cardService.listByUser(userId)
        Ok(Json.generate(list))
      } else {
        Unauthorized("You are not authorized for viewing the card list")
      }
  }

  def get(number: String, userId: Int) = Authenticated {
    request =>
      if (userId != request.user.userId) {
        BadRequest("You are not authorized for viewing the card")
      } else {
        cardService.get(number, userId) match {
          case Some(card) => Ok(Json.generate(card))
          case _ => BadRequest("Could not find the card")
        }
      }

  }

  def savePin(number: String, userId: Int) = Authenticated(parse.json) {
    request =>
      cardService.get(number, userId) match {
        case Some(card) =>
          cardService.savePin((request.body \ "pinCode").as[String], number)
          Ok("ok")
        case _ => BadRequest("You are not authorized to save pin code for the card")
      }
  }
}
