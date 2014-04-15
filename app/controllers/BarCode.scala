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

class BarCode @Inject()(cardService: service.common.CardService) extends BaseController {
  def generateBarCode(content: String): Array[Byte] = {
    val bc = new PDF417Bean
    bc.setModuleWidth(4)
    bc.setRowHeight(3)
    bc.setMinCols(1)
    bc.setMaxCols(1)
    bc.setMinRows(3)
    bc.setMaxRows(90)
    bc.setErrorCorrectionLevel(2)
    val out = new ByteArrayOutputStream()
    val image = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB)
    val g2 = image.createGraphics()
    g2.setBackground(Color.WHITE)
    g2.clearRect(0, 0, image.getWidth, image.getHeight)
    g2.setColor(Color.BLACK)
    bc.generateBarcode(new Java2DCanvasProvider(g2, 0), content)
    ImageIO.write(image, "png", out)
    out.toByteArray
  }

  //starbucks format for barcode generation: {card number}={card pin code}=1
  def getPdf47(content: String) = Action {
    val imageData: Array[Byte] = generateBarCode(s";$content?")
    Ok(imageData).withHeaders(
      CONTENT_TYPE -> "image/png",
      CONTENT_DISPOSITION -> "inline",
      CACHE_CONTROL -> "public, max-age=86400"
    )
  }

  def cardBarCode(number: String, userId: Int) = authenticated {
    user =>
      request =>
        cardService.get(number, userId) match {
          case Some(card) =>
            Ok(generateBarCode(s";$number=${card.pinCode}=1?")).withHeaders(
              CONTENT_TYPE -> "image/png",
              CONTENT_DISPOSITION -> "inline",
              CACHE_CONTROL -> "public, max-age=86400"
            )
          case _ => BadRequest("Could not find the card")
        }
  }
}
