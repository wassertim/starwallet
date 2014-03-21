package service

import play.api.libs.ws.WS
import org.jsoup.Jsoup
import model._
import org.joda.time.format.DateTimeFormat

import org.jsoup.nodes.Document
import scala.collection.JavaConversions._
import com.ning.http.client.Cookie


import model.Transaction
import model.Coupon
import model.StarbucksAccount
import scala.Some
import model.AuthInfo
import model.Card
import javax.net.ssl._
import java.security.cert.X509Certificate
import java.security.{SecureRandom, Security}
import model.Transaction
import model.Coupon
import model.StarbucksAccount
import scala.Some
import model.AuthInfo
import model.Card


class Starbucks extends service.common.Starbucks {
  val mainUrl = "https://plas-tek.ru/cabinet.aspx?style=starbucks"

  private def auth(userName: String, password: String) = {
    val loginUrl = s"$mainUrl&mainlogin=true"
    val loginPageResponse = WS.client.prepareGet(loginUrl).execute().get()
    val (loginDoc, cookies) = (Jsoup.parse(loginPageResponse.getResponseBody), loginPageResponse.getCookies)

    val authenticatedResponse = WS.client.preparePost(loginUrl)
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
    cookies.map(cookie => authenticatedResponse.addCookie(cookie))
    (Jsoup.parse(authenticatedResponse.execute().get.getResponseBody), cookies)
  }

  private def changeScreen(screenName: String, cardsPage: Document, cookies: Seq[Cookie]) = {
    val response = WS.client.preparePost(mainUrl)
      .addParameter("ToolkitScriptManager1_HiddenField", cardsPage.getElementsByAttributeValue("name", "ToolkitScriptManager1_HiddenField").`val`)
      .addParameter("offset", cardsPage.getElementsByAttributeValue("name", "offset").`val`)
      .addParameter("__EVENTTARGET", screenName)
      .addParameter("__EVENTARGUMENT", cardsPage.getElementsByAttributeValue("name", "__EVENTARGUMENT").`val`)
      .addParameter("__EVENTVALIDATION", cardsPage.getElementsByAttributeValue("name", "__EVENTVALIDATION").`val`)
      .addParameter("__VIEWSTATE", cardsPage.getElementsByAttributeValue("name", "__VIEWSTATE").`val`())
    cookies.map(cookie => response.addCookie(cookie))
    val doc = Jsoup.parse(response.execute().get.getResponseBody)
    doc
  }

  private def cardList(cardsPage: Document, cookies: Seq[Cookie]): List[Card] = {
    getCard(cardsPage) :: cardsPage.select(".card_numbers a").tail.map(
      a =>
        getCard(changeScreen(a.attr("id").replace("_", "$"), cardsPage, cookies))
    ).toList
  }

  private def getDouble(value: String) = {
    "[0-9]+\\.[0-9][0-9]".r findFirstIn value.trim.replace(",", ".") match {
      case Some(x) => x.toDouble
      case None => 0d
    }
  }

  private def getCard(cardsPage: Document): Card = {
    val cardNumber: String = cardsPage.select("#lblCardNumber").text
    val cardBalance: Double = cardsPage.select("#lblCardBalance").text.replace(",", ".").replace(" руб.", "").toDouble
    val isActive: Boolean = cardsPage.select("#lblCardStatus").text.trim.toLowerCase == "активна"
    val transactionList: Seq[Transaction] = {
      val trs = cardsPage.select(".data_table .td, .data_table .alt")
      trs.map {
        tr =>
          val tds = tr.select("td")
          val formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
          Transaction(
            formatter.parseDateTime(tds.get(0).text.trim),
            tds.get(1).text.trim,
            tds.get(2).text.trim,
            getDouble(tds.get(3).text),
            getDouble(tds.get(4).text)
          )
      }
    }
    Card(cardNumber, cardBalance, isActive, transactionList)
  }

  private def couponList(cardsPage: Document, cookies: Seq[Cookie]): Seq[Coupon] = {
    val couponsPage = changeScreen("btnCouponsTab", cardsPage, cookies)
    val trs = couponsPage.select(".data_table .td, .data_table .alt")
    trs.map {
      tr =>
        val tds = tr.select("td")
        val formatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        Coupon(
          tds.get(0).text.trim,
          formatter.parseDateTime(tds.get(1).text.trim),
          formatter.parseDateTime(tds.get(2).text.trim),
          tds.get(3).text.trim.toLowerCase == "активен",
          tds.get(4).text.trim
        )
    }
  }

  private def starsCount(implicit cardsPage: Document): Int = {
    val starsText = cardsPage.select("#pnlCardInfo > div").text
    "[0-9]+".r findFirstIn starsText match {
      case Some(x) => x.toInt
      case None => 0
    }
  }

  def getAccountData(authInfo: AuthInfo) = {
    val (userName, password) = (authInfo.userName, authInfo.password)
    implicit val (cardsPage, cookies) = auth(userName, password)
    StarbucksAccount(authInfo.userName, starsCount, cardList(cardsPage, cookies), couponList(cardsPage, cookies))
  }
}

