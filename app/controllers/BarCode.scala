package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import org.krysalis.barcode4j.impl.pdf417._
import java.io.ByteArrayOutputStream
import java.awt.Color
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import controllers.common.BaseController
import org.krysalis.barcode4j.BarcodeDimension
import utility.BarCodeUtility

class BarCode @Inject()(cardService: service.common.CardService) extends BaseController {

  def cardBarCode(number: String, userId: Int) = authenticated {
    user =>
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
