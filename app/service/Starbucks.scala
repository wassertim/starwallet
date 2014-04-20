package service

import play.api.libs.ws.WS
import org.jsoup.Jsoup
import model._
import org.joda.time.format.DateTimeFormat

import org.jsoup.nodes.{Element, Document}
import scala.collection.JavaConversions._
import com.ning.http.client.{AsyncHandler, Cookie}

import scala.Some
import org.joda.time.DateTime
import java.sql.Timestamp
import java.util
import org.jsoup.select.Elements
import play.api.libs.concurrent.Execution.Implicits._

class Starbucks extends service.common.Starbucks {
  import utility.DateTimeUtility.ts

  val mainUrl = "https://cabinet.plas-tek.ru/default.aspx?style=starbucks"

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
            ts(formatter.parseDateTime(tds.get(0).text.trim)),
            tds.get(1).text.trim,
            tds.get(2).text.trim,
            getDouble(tds.get(3).text),
            getDouble(tds.get(4).text)
          )
      }
    }
    Card(cardNumber, cardBalance, isActive, "", transactionList)
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

  override def getAccount(authInfo: AuthInfo): Option[StarbucksAccount] = {
    val now = new Timestamp(new java.util.Date().getTime)
    authenticate(authInfo).fold(
      page => {
        Some(StarbucksAccount(authInfo.userName, starsCount(page.document), cardList(page.document, page.cookies), couponList(page.document, page.cookies), now))
      },
      error => None
    )

  }
  class MapWrapper {
    val map = new util.HashMap[String, String]()
    def addParameter(key: String, value: String) = {
      map.put(key, value)
      this
    }
  }

  def getParams(authInfo: RegAuthInfo, registrationDoc: Document) = {

    def field(value: String) = {
      Seq1(registrationDoc.getElementsByAttributeValue("name", value).`val`)
    }
    def Seq1(value: String) = Seq(value) //Seq(java.net.URLEncoder.encode(value, "UTF-8"))
    val fmt = DateTimeFormat.forPattern("dd.MM.yyyy")
    val date = fmt.print(DateTime.now.plusDays(1).minusYears(30))
    Map(
      "ToolkitScriptManager1_HiddenField" -> field("ToolkitScriptManager1_HiddenField"),
      "offset" -> Seq1(""),
      "__EVENTTARGET" -> Seq1(""),
      "__EVENTARGUMENT" -> field("__EVENTARGUMENT"),
      "__EVENTVALIDATION" -> field("__EVENTVALIDATION"),
      "__VIEWSTATE" -> field("__VIEWSTATE"),
      "ImageButton12" -> field("ImageButton12"),
      "ImageButton5" -> field("ImageButton5"),

      "RegistrationCardNumberTextBox" -> Seq1(authInfo.cardNumber),
      "RegistrationPinTextBox" -> Seq1(authInfo.pinCode),
      "RegistrationLoginTextBox" -> Seq1(authInfo.userName),
      "RegistrationPasswordTextBox" -> Seq1(authInfo.password),
      "tbRegistrationPasswordConfirmation" -> Seq1(authInfo.password),
      "RegistrationNameTextBox" -> Seq1(authInfo.firstName),
      "RegistrationSurnameTextBox" -> Seq1(authInfo.lastName),
      "RegistrationBirthDateTextBox" -> Seq1(date),
      "RegistrationSexDropDownList" -> Seq1("1"),
      "RegistrationAddressTextBox" -> Seq1("Улица Ленина"),
      "ddlRegistrationPhoneNumberCode" -> Seq1("7"),
      "RegistrationPhoneNumberTextBox" -> Seq1(authInfo.phoneNumber),
      "ddlRegistrationSecretQuestion" -> Seq1("3"),
      "tbRegistrationSecretQuestion" -> Seq1("123456"),
      "RegistrationProfessionTextBox" -> Seq1("Рабочий"),
      "tbRegistrationEmail" -> Seq1(authInfo.email),
      "tbRegistrationEmailConfirmation" -> Seq1(authInfo.email),
      "ddlSurvey" -> Seq1("3"),
      "NotifyCheckBox" -> Seq1("on"),
      "chbSendCouponByEmail" -> Seq1("on"),
      "chkAccept" -> Seq1("on"),

      "btnRegister.x" -> Seq1("46"),
      "btnRegister.y" -> Seq1("11"),
      "TextBoxWatermarkExtender1_ClientState" -> field("TextBoxWatermarkExtender1_ClientState"),
      "TextBoxWatermarkExtender13_ClientState" -> field("TextBoxWatermarkExtender13_ClientState"),
      "TextBoxWatermarkExtender4_ClientState" -> field("TextBoxWatermarkExtender4_ClientState"),
      "TextBoxWatermarkExtender5_ClientState" -> field("TextBoxWatermarkExtender5_ClientState"),
      "meeBirthDate_ClientState" -> field("meeBirthDate_ClientState"),
      "TextBoxWatermarkExtender7_ClientState" -> field("TextBoxWatermarkExtender7_ClientState"),
      "meePhone_ClientState" -> field("meePhone_ClientState"),
      "TextBoxWatermarkExtender8_ClientState" -> field("TextBoxWatermarkExtender8_ClientState"),
      "TextBoxWatermarkExtender9_ClientState" -> field("TextBoxWatermarkExtender9_ClientState"),
      "TextBoxWatermarkExtender10_ClientState" -> field("TextBoxWatermarkExtender10_ClientState"),
      "TextBoxWatermarkExtender102_ClientState" -> field("TextBoxWatermarkExtender102_ClientState"),
      "TextBoxWatermarkExtender6_ClientState" -> field("TextBoxWatermarkExtender6_ClientState")
    )
  }
  def register(authInfo: RegAuthInfo) = {
    import WS.client
    val registrationUrl = s"$mainUrl&registration=true"
    val pageResponse = client.prepareGet(registrationUrl).execute.get
    val (registrationDoc, cookies) = (Jsoup.parse(pageResponse.getResponseBody), pageResponse.getCookies)
    val requestBuilder = cookies.foldLeft(
      getParams(authInfo, registrationDoc).foldLeft(client.preparePost(registrationUrl)) {
        (requestBuilder, params) =>

          requestBuilder.addParameter(params._1, params._2.head)
      }
    ) {
      (r, c) =>
        r.addCookie(c)
    }
      .addHeader("Content-Type", "application/x-www-form-urlencoded ; charset=UTF-8")
      .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36")
      .addHeader("Accept-Charset", "utf-8")
      .addHeader("Host", "cabinet.plas-tek.ru")
      .addHeader("Cache-Control", "no-cache")
      .addHeader("Pragma", "no-cache")
      .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
      .addHeader("Accept-Language", "en-US,en;q=0.8,ru;q=0.6,de;q=0.4,uk;q=0.2")
      .addHeader("Origin", "https://cabinet.plas-tek.ru")

    val response = requestBuilder.execute.get
    val result = Jsoup.parse(response.getResponseBody)
    if (result.select("#pnlMessage").size() > 0){
      val text = result.select("#pnlMessage").text
      val t = text
    } else {
      val text = "no error message"
    }
  }

  def register2(authInfo: RegAuthInfo) = {
    val registrationUrl = s"$mainUrl&registration=true"
    WS.url(registrationUrl).get map {
      pageResponse =>
        val (registrationDoc, cookies) = (Jsoup.parse(pageResponse.body), pageResponse.cookies)
        val params = getParams(authInfo, registrationDoc).map(e => s"${e._1}=${e._2.head}").mkString("&")
        val c = cookies.map(cookie => s"${cookie.name.get}=${cookie.value.get}").mkString("; ")

        WS.url(registrationUrl)
          .withHeaders(
            "User-Agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36",
            //"Content-Type" -> "application/x-www-form-urlencoded",
            /*"Content-Type" -> "multipart/form-data",*/
            "Host" -> "cabinet.plas-tek.ru",
            "Cache-Control" -> "no-cache",
            "Pragma" -> "no-cache",
            "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Language" -> "en-US,en;q=0.8,ru;q=0.6,de;q=0.4,uk;q=0.2",
            "Origin" -> "https://cabinet.plas-tek.ru",
            "Accept-Charset" -> "utf-8",
            //"Content-Length" -> params.length.toString,
            "Cookie" -> c
          ).post(params) map {
          response =>
            if (response.getAHCResponse.getUri.getPath().contains("error")) {
              val result = Jsoup.parse(response.getAHCResponse.getResponseBody("utf-8"))
              val e = result
            } else {
              val result = Jsoup.parse(response.body)
              val e = result
            }
        }
    }
  }
}




