package service

import org.jsoup.Jsoup
import play.api.libs.ws.WS
import model.RegistrationInfo
import org.joda.time.DateTime
import utility.DateTimeUtility
import org.jsoup.nodes.Document
import scala.collection.JavaConversions._
import play.api.libs.concurrent.Execution.Implicits._

class RegistrationService extends common.RegistrationService {

  val mainUrl = "https://cabinet.plas-tek.ru/default.aspx?style=starbucks"
  val registrationUrl = s"$mainUrl&registration=true"

  def getParams(authInfo: RegistrationInfo, registrationDoc: Document) = {
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
      "RegistrationLoginTextBox" -> Seq(authInfo.userName),
      "RegistrationPasswordTextBox" -> Seq(authInfo.password),
      "tbRegistrationPasswordConfirmation" -> Seq(authInfo.password),
      "RegistrationNameTextBox" -> Seq(authInfo.firstName),
      "RegistrationSurnameTextBox" -> Seq(authInfo.lastName),
      "RegistrationBirthDateTextBox" -> Seq(DateTimeUtility.formatted(DateTime.now.plusDays(1).minusYears(30))),
      "RegistrationSexDropDownList" -> Seq("1"),
      "RegistrationAddressTextBox" -> Seq("Улица Ленина"),
      "ddlRegistrationPhoneNumberCode" -> Seq("7"),
      "RegistrationPhoneNumberTextBox" -> Seq(authInfo.phoneNumber),
      "ddlRegistrationSecretQuestion" -> Seq("3"),
      "tbRegistrationSecretQuestion" -> Seq("123456"),
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

  def getHeaders(cookies: String) = {
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

  def register(authInfo: RegistrationInfo) = WS.url(registrationUrl).get map {
    pageResponse =>
      val (registrationDoc, cookies) = (Jsoup.parse(pageResponse.body), pageResponse.cookies)
      val params = getParams(authInfo, registrationDoc).map {
        e =>
          val (key, value) = (e._1, e._2.head)
          s"${key}=${java.net.URLEncoder.encode(value, "UTF-8")}"
      }.mkString("&")
      val c = cookies.map(cookie => s"${cookie.name.get}=${cookie.value.get}").mkString("; ")
      getHeaders(c).foldLeft(WS.url(registrationUrl)) {
        (requestHolder, header) =>
          requestHolder.withHeaders(header)
      }.post(params) map {
        response =>
          if (response.getAHCResponse.getUri.getPath.contains("error")) {
            val result = Jsoup.parse(response.getAHCResponse.getResponseBody("utf-8"))
          } else {
            val result = Jsoup.parse(response.body)
            val e = result
            if (result.select("#pnlMessage").size() > 0) {
              val text = result.select("#pnlMessage").text
              val t = text
            } else {
              val text = "no error message"
            }
          }
      }
  }
  //Experimental
  def register2(authInfo: RegistrationInfo) = {
    import WS.client

    val pageResponse = client.prepareGet(registrationUrl).execute.get
    val (registrationDoc, cookies) = (Jsoup.parse(pageResponse.getResponseBody), pageResponse.getCookies)
    val cookieString = cookies.map(cookie => s"${cookie.getName}=${cookie.getValue}").mkString("; ")
    val requestWithParams = getParams(authInfo, registrationDoc).foldLeft(client.preparePost(registrationUrl)) {
      (requestBuilder, params) => requestBuilder.addParameter(params._1, params._2.head)
    }
    val requestWithHeaders = getHeaders(cookieString).foldLeft(requestWithParams) {
      (r, h) => r.addHeader(h._1, h._2)
    }
    val response = requestWithHeaders.execute.get
    val result = Jsoup.parse(response.getResponseBody)
    if (result.select("#pnlMessage").size() > 0) {
      val text = result.select("#pnlMessage").text
      val t = text
    } else {
      val text = "no error message"
    }
  }
}
