package service.http

import model._
import scala.Some
import org.jsoup.Jsoup
import java.sql.Timestamp
import play.api.libs.ws.WS
import com.ning.http.client.Cookie
import scala.collection.JavaConversions._
import org.jsoup.nodes.{Element, Document}
import org.joda.time.format.DateTimeFormat

class Starbucks extends service.common.http.Starbucks {

  import utility.DateTimeUtility.ts

  val mainUrl = "https://cabinet.plas-tek.ru/default.aspx?style=starbucks"

  val registrationUrl = s"$mainUrl&registration=true"

  def authenticate(authInfo: AuthInfo) = {
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
      .addParameter("LoginTextBox1", authInfo.userName)
      .addParameter("PasswordTextBox2", authInfo.password)
      .addParameter("LoginLinkButtonDynamic", "Войти")
    cookies.map(cookie => authenticatedResponse.addCookie(cookie))
    val result = Jsoup.parse(authenticatedResponse.execute().get.getResponseBody)
    result.select("#pnlMessage").text.trim match {
      case "Ошибка : пользователь не зарегистрирован в системе!" => Right(AuthError("invalid auth data"))
      case "Ошибка : введено неправильное имя пользователя или пароль." => Right(AuthError("invalid auth data"))
      case "" => Left(Page(result, cookies))
    }
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
    Card(
      CardData(cardsPage.select("#lblCardNumber").text, ""),
      cardsPage.select("#lblCardBalance").text.replace(",", ".").replace(" руб.", "").toDouble,
      cardsPage.select("#lblCardStatus").text.trim.toLowerCase == "активна",
      cardsPage.select(".data_table .td, .data_table .alt").map {
        tr =>
          val tds = tr.select("td")
          val formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
          Transaction(
            ts(formatter.parseDateTime(tds.get(0).text.trim)),
            tds.get(1).text.trim,
            tds.get(2).text.trim,
            getDouble(tds.get(3).text),
            getDouble(tds.get(4).text)
          )
      }
    )
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
          ts(formatter.parseDateTime(tds.get(1).text.trim)),
          ts(formatter.parseDateTime(tds.get(2).text.trim)),
          tds.get(3).text.trim.toLowerCase == "активен",
          tds.get(4).text.trim,
          getKey(tds.get(5))
        )
    }
  }


  private def getKey(el: Element) = {
    "Q[0-9]*[A-Za-z0-9]*".r findFirstIn el.select("a").attr("href") match {
      case Some(x) => x
      case _ => ""
    }

  }

  private def starsCount(implicit cardsPage: Document): Int = {
    val starsText = cardsPage.select("#pnlCardInfo > div").text
    "[0-9]+".r findFirstIn starsText match {
      case Some(x) => x.toInt
      case None => 0
    }
  }

  def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]] = {
    val now = new Timestamp(new java.util.Date().getTime)
    authenticate(authInfo).fold(
      page => {
        Some(StarbucksAccount(authInfo.userName, starsCount(page.document), cardList(page.document, page.cookies), couponList(page.document, page.cookies), now))
      },
      error => None
    )

  }
}




