package service

import play.api.libs.ws.WS
import org.jsoup.Jsoup
import model.{StarbucksAccount, Transaction, Coupon}
import org.joda.time.format.DateTimeFormat
import java.text.DecimalFormat

class Starbucks extends service.common.Starbucks {
  def auth(userName: String, password: String) = {
    import scala.collection.JavaConversions._
    val loginUrl = "https://plas-tek.ru/cabinet.aspx?mainlogin=true&style=stylestarbucks"
    val mainUrl = "https://plas-tek.ru/cabinet.aspx?style=starbucks"
    val (loginDoc, cookies) = getLoginPageResponse
    val cardsPage = auth
    val couponsPage = changeTab("btnCouponsTab")

    def changeTab(tabName: String) = {
      val response = WS.client.preparePost(mainUrl)
        .addParameter("ToolkitScriptManager1_HiddenField", cardsPage.getElementsByAttributeValue("name", "ToolkitScriptManager1_HiddenField").`val`)
        .addParameter("offset", cardsPage.getElementsByAttributeValue("name", "offset").`val`)
        .addParameter("__EVENTTARGET", tabName)
        .addParameter("__EVENTARGUMENT", cardsPage.getElementsByAttributeValue("name", "__EVENTARGUMENT").`val`)
        .addParameter("__EVENTVALIDATION", cardsPage.getElementsByAttributeValue("name", "__EVENTVALIDATION").`val`)
        .addParameter("__VIEWSTATE", cardsPage.getElementsByAttributeValue("name", "__VIEWSTATE").`val`())
      cookies.map(cookie => response.addCookie(cookie))
      Jsoup.parse(response.execute().get.getResponseBody)
    }

    def getLoginPageResponse = {
      val response = WS.client.prepareGet(loginUrl).execute().get()
      (Jsoup.parse(response.getResponseBody), response.getCookies)
    }

    def auth = {
      val response = WS.client.preparePost(loginUrl)
        .addParameter("ToolkitScriptManager1_HiddenField", loginDoc.getElementsByAttributeValue("name", "ToolkitScriptManager1_HiddenField").`val`)
        .addParameter("offset", loginDoc.getElementsByAttributeValue("name", "offset").`val`)
        .addParameter("__EVENTTARGET", loginDoc.getElementsByAttributeValue("name", "__EVENTTARGET").`val`)
        .addParameter("__EVENTARGUMENT", loginDoc.getElementsByAttributeValue("name", "__EVENTARGUMENT").`val`)
        .addParameter("__EVENTVALIDATION", loginDoc.getElementsByAttributeValue("name", "__EVENTVALIDATION").`val`)
        .addParameter("__VIEWSTATE", loginDoc.getElementsByAttributeValue("name", "__VIEWSTATE").`val`())
        .addParameter("ImageButton19", "")
        .addParameter("ImageButton9", "")
        .addParameter("LoginTextBox1", userName)
        .addParameter("PasswordTextBox2", password)
        .addParameter("LoginLinkButtonDynamic", "Войти")
      cookies.map(cookie => response.addCookie(cookie))
      Jsoup.parse(response.execute().get.getResponseBody)
    }

    def starsCount: Int = {
      val starsText = cardsPage.select("#pnlCardInfo > div").text
      "[0-9]+".r findFirstIn starsText match {
        case Some(x) => x.toInt
        case None => 0
      }
    }

    def cardNumber: String = cardsPage.select("#lblCardNumber").text

    def cardBalance: Double = cardsPage.select("#lblCardBalance").text.replace(",", ".").replace(" руб.", "").toDouble

    def cardStatus: String = cardsPage.select("#lblCardStatus").text

    def couponList: Seq[Coupon] = {
      val trs = couponsPage.select(".data_table .td, .data_table .alt")
      trs.map {
        tr =>
          val tds = tr.select("td")
          val formatter = DateTimeFormat.forPattern("dd.MM.yyyy")
          Coupon(
            tds.get(0).text.trim,
            formatter.parseDateTime(tds.get(1).text.trim),
            formatter.parseDateTime(tds.get(2).text.trim),
            tds.get(3).text.trim,
            tds.get(4).text.trim
          )
      }

    }

    def transactionList: Seq[Transaction] = {
      val trs = cardsPage.select(".data_table .td, .data_table .alt")
      trs.map {
        tr =>
          val tds = tr.select("td")
          val formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
          val dformatter = new DecimalFormat("#.##")
          Transaction(
            formatter.parseDateTime(tds.get(0).text.trim),
            tds.get(1).text.trim,
            tds.get(2).text.trim,
            tds.get(3).text.trim.replace(",", ".").toDouble
          )
      }
    }

    StarbucksAccount(cardNumber, starsCount, cardBalance, cardStatus, couponList, transactionList)
  }
}

