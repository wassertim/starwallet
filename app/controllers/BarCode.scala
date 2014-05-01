package controllers

import com.google.inject.Inject
import controllers.common.BaseController
import utility.{Authorized, BarCodeUtility}

class BarCode @Inject()(cardService: service.common.CardService) extends BaseController {

  def cardBarCode(number: String, userId: Int) = Authorized(userIds = Seq(userId), roles = Seq("admin")) {
    request =>
      cardService.get(number, userId) match {
        case Some(card) =>
          Ok(BarCodeUtility.generate(s";$number=${card.data.pin}=1?")).withHeaders(
            CONTENT_TYPE -> "image/png",
            CONTENT_DISPOSITION -> "inline",
            CACHE_CONTROL -> "public, max-age=86400"
          )
        case _ => BadRequest("Could not find the card")
      }
  }
}
