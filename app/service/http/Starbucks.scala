package service.http

import model._

import java.net.URLEncoder._
import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import com.ning.http.client.Cookie
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.{Response, WS}
import utility.DateTimeUtility
import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Random

class Starbucks extends service.common.http.Starbucks {

  import utility.DateTimeUtility.ts

  val mainUrl = "https://cabinet.plas-tek.ru/default.aspx?style=starbucks"

  val regUrl = s"$mainUrl&registration=true"

  def authenticate(authInfo: AuthInfo) = {
    authenticateInternal(authInfo).fold(
      page => Left(),
      message => Right(message)
    )
  }
  private val authErrors = Seq(
    "Ошибка : пользователь не зарегистрирован в системе!",
    "Ошибка : введено неправильное имя пользователя или пароль.",
    "Ошибка : личный кабинет не активирован! Пройдите регистрацию до конца, воспользовавшись ссылкой отправленной Вам на e-mail."
  )
  private def authenticateInternal(authInfo: AuthInfo) = {
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
    val errorMessage = result.select("#pnlMessage").text.trim
    if (!errorMessage.isEmpty) {
      authErrors.find(message => errorMessage.contains(message)) match {
        case Some(error) => error match {
          case "Ошибка : пользователь не зарегистрирован в системе!" => Right(AuthError("invalid auth data"))
          case "Ошибка : введено неправильное имя пользователя или пароль." => Right(AuthError("invalid auth data"))
          case "Ошибка : личный кабинет не активирован! Пройдите регистрацию до конца, воспользовавшись ссылкой отправленной Вам на e-mail." => Right(AuthError("not active"))
          case e => Right(AuthError(e))
        }
        case None => Right(AuthError("error"))
      }
    } else {
      Left(Page(result, cookies))
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
    val starsText = cardsPage.select("#pnlCardInfo div").text
    "[0-9]+".r findFirstIn starsText match {
      case Some(x) => x.toInt
      case None => 0
    }
  }

  def getAccount(authInfo: AuthInfo): Option[StarbucksAccount[Card]] = {
    val now = new Timestamp(new java.util.Date().getTime)
    authenticateInternal(authInfo).fold(
      page => {
        Some(StarbucksAccount(authInfo.userName, starsCount(page.document), cardList(page.document, page.cookies), couponList(page.document, page.cookies), now))
      },
      error => None
    )

  }

  def activate(url: String) = {
    WS.url(url).get().map {
      response =>
        val result = Jsoup.parse(response.body)
        val pnlMessage = result.select("#pnlMessage")
        if (pnlMessage.size() > 0)
          pnlMessage.text match {
            case m if m.contains("Регистрация успешно завершена, Ваш личный кабинет активирован") => Left()
            case m => Right(m)
          }
        else
          Right("unknownError")
    }
  }

  def register(authInfo: RegistrationInfo) = {
    val promise = WS.url(regUrl).get()
    val pageResponse = Await.result(promise, Duration(20000, TimeUnit.MILLISECONDS))
    val (registrationDoc, cookies) = (Jsoup.parse(pageResponse.body), pageResponse.cookies)
    val params = getParams(authInfo, registrationDoc).map {
      e =>
        val (key, value) = (e._1, e._2.head)
        s"$key=${encode(value, "UTF-8")}"
    }.mkString("&")
    val c = cookies.map(cookie => s"${cookie.name.get}=${cookie.value.get}").mkString("; ")
    getHeaders(c).foldLeft(WS.url(regUrl))((requestHolder, header) => requestHolder.withHeaders(header)).post(params).map {
      r => handleResponse(r)
    }
  }

  private def randomAnswer = {
    val random = new Random()
    Math.abs(random.nextLong()).toString
  }

  private def getParams(authInfo: RegistrationInfo, registrationDoc: Document) = {
    def fromField(value: String) = Seq(registrationDoc.getElementsByAttributeValue("name", value).`val`)
    Map(
      "ToolkitScriptManager1_HiddenField" -> fromField("ToolkitScriptManager1_HiddenField"),
      "offset" -> Seq(""),
      "__EVENTTARGET" -> Seq(""),
      "__EVENTARGUMENT" -> fromField("__EVENTARGUMENT"),
      "__EVENTVALIDATION" -> fromField("__EVENTVALIDATION"),
      "__VIEWSTATE" -> fromField("__VIEWSTATE"),
      "ImageButton12" -> fromField("ImageButton12"),
      "ImageButton5" -> fromField("ImageButton5"),
      "TextBoxWatermarkExtender1_ClientState" -> fromField("TextBoxWatermarkExtender1_ClientState"),
      "TextBoxWatermarkExtender13_ClientState" -> fromField("TextBoxWatermarkExtender13_ClientState"),
      "TextBoxWatermarkExtender4_ClientState" -> fromField("TextBoxWatermarkExtender4_ClientState"),
      "TextBoxWatermarkExtender5_ClientState" -> fromField("TextBoxWatermarkExtender5_ClientState"),
      "meeBirthDate_ClientState" -> fromField("meeBirthDate_ClientState"),
      "TextBoxWatermarkExtender7_ClientState" -> fromField("TextBoxWatermarkExtender7_ClientState"),
      "meePhone_ClientState" -> fromField("meePhone_ClientState"),
      "TextBoxWatermarkExtender8_ClientState" -> fromField("TextBoxWatermarkExtender8_ClientState"),
      "TextBoxWatermarkExtender9_ClientState" -> fromField("TextBoxWatermarkExtender9_ClientState"),
      "TextBoxWatermarkExtender10_ClientState" -> fromField("TextBoxWatermarkExtender10_ClientState"),
      "TextBoxWatermarkExtender102_ClientState" -> fromField("TextBoxWatermarkExtender102_ClientState"),
      "TextBoxWatermarkExtender6_ClientState" -> fromField("TextBoxWatermarkExtender6_ClientState"),

      "RegistrationCardNumberTextBox" -> Seq(authInfo.card.number),
      "RegistrationPinTextBox" -> Seq(authInfo.card.pin),
      "RegistrationLoginTextBox" -> Seq(authInfo.auth.userName),
      "RegistrationPasswordTextBox" -> Seq(authInfo.auth.password),
      "tbRegistrationPasswordConfirmation" -> Seq(authInfo.auth.password),
      "RegistrationNameTextBox" -> Seq(authInfo.firstName),
      "RegistrationSurnameTextBox" -> Seq(authInfo.lastName),
      "RegistrationBirthDateTextBox" -> Seq(DateTimeUtility.formatted(DateTime.now.plusDays(1).minusYears(30))),
      "RegistrationSexDropDownList" -> Seq("1"),
      "RegistrationAddressTextBox" -> Seq("Улица Ленина"),
      "ddlRegistrationPhoneNumberCode" -> Seq("7"),
      "RegistrationPhoneNumberTextBox" -> Seq(authInfo.phoneNumber),
      "ddlRegistrationSecretQuestion" -> Seq("3"),
      "tbRegistrationSecretQuestion" -> Seq(randomAnswer),
      "RegistrationProfessionTextBox" -> Seq("Рабочий"),
      "tbRegistrationEmail" -> Seq(authInfo.email),
      "tbRegistrationEmailConfirmation" -> Seq(authInfo.email),
      "ddlSurvey" -> Seq("3"),
      "NotifyCheckBox" -> Seq("on"),
      "chbSendCouponByEmail" -> Seq("on"),
      "chkAccept" -> Seq("on"),
      "btnRegister.x" -> Seq("46"),
      "btnRegister.y" -> Seq("11")
    )
  }

  private def getHeaders(cookies: String) = {
    Map(
      "User-Agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36",
      "Content-Type" -> "application/x-www-form-urlencoded ; charset=UTF-8",
      "Host" -> "cabinet.plas-tek.ru",
      "Cache-Control" -> "no-cache",
      "Pragma" -> "no-cache",
      "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
      "Accept-Language" -> "en-US,en;q=0.8,ru;q=0.6,de;q=0.4,uk;q=0.2",
      "Origin" -> "https://cabinet.plas-tek.ru",
      "Accept-Charset" -> "utf-8",
      "Cookie" -> cookies
    )
  }

  def handleResponse(response: Response): Either[Unit, String] = {
    if (response.getAHCResponse.getUri.getPath.contains("error")) {
      Right("unknownError")
    } else {
      val result = Jsoup.parse(response.body)
      val pnlMessage = result.select("#pnlMessage")
      if (pnlMessage.size() > 0) {
        pnlMessage.text match {
          case m if m.contains("Ваши данные приняты") => {
            Left()
          }
          case m => Right(m)
        }
      } else {
        Right("unknownError")
      }
    }
  }
}




