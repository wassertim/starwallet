package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import utility.Authorized
import model.CardListItem.writes
import service.common.sql.CardService
import play.api.libs.json.Json

class Card @Inject()(cardService: CardService) extends BaseController {

  def list(userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      Ok(Json.toJson(cardService.listByUser(userId)))
  }

  def get(number: String, userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      cardService.get(number, userId) match {
        case Some(card) => Ok(Json.toJson(card))
        case _ => BadRequest("Could not find the card")
      }
  }

  def savePin(number: String, userId: Int) = Authorized(parse.json)(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      cardService.savePin((request.body \ "pinCode").as[String], number)
      Ok("ok")
  }
}
