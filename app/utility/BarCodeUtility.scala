package utility

import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean

object BarCodeUtility {

  def generate(content: String): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val image = getBarCodeImage(content)
    ImageIO.write(image, "png", out)
    out.toByteArray
  }

  private def getBarCodeImage(content: String) = {
    val pdf417 = createGenerator
    val size = pdf417.calcDimensions(content)
    val image = new BufferedImage(size.getWidth.toInt, size.getHeight.toInt, BufferedImage.TYPE_INT_RGB)
    val graphics2D = image.createGraphics()
    graphics2D.setBackground(Color.WHITE)
    graphics2D.clearRect(0, 0, image.getWidth, image.getHeight)
    graphics2D.setColor(Color.BLACK)
    pdf417.generateBarcode(new Java2DCanvasProvider(graphics2D, 0), content)
    image
  }

  private def createGenerator = {
    val pDF417Bean = new PDF417Bean
    pDF417Bean.setModuleWidth(4)
    pDF417Bean.setRowHeight(3)
    pDF417Bean.setMinCols(1)
    pDF417Bean.setMaxCols(1)
    pDF417Bean.setMinRows(3)
    pDF417Bean.setMaxRows(90)
    pDF417Bean.doQuietZone(true)
    pDF417Bean.setQuietZone(1)
    pDF417Bean.setErrorCorrectionLevel(2)
    pDF417Bean
  }
}
