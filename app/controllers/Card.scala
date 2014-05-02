package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import utility.Authorized
import utility.JsonResults._

class Card @Inject()(cardService: service.common.CardService) extends BaseController {

  def list(userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      json(cardService.listByUser(userId))
  }

  def get(number: String, userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      cardService.get(number, userId) match {
        case Some(card) => json(card)
        case _ => BadRequest("Could not find the card")
      }
  }

  def savePin(number: String, userId: Int) = Authorized(parse.json)(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      cardService.savePin((request.body \ "pinCode").as[String], number)
      Ok("ok")
  }
}
