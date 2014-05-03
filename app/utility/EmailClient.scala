package utility

import java.util.Properties
import javax.mail.{Multipart, Folder, Session}
import com.sun.mail.pop3.POP3Store
import javax.mail.Message.RecipientType

class EmailClient(userName: String, password: String) {
  lazy val emailStore = createEmailStore
  lazy val mailBox = connect()

  private def createEmailStore = {
    val session = Session.getDefaultInstance(getSettings("pop.gmail.com", "995"))
    session.getStore("pop3").asInstanceOf[POP3Store]
  }

  private def getSettings(host: String, port: String) = {
    // sets POP3 properties
    val properties = new Properties()
    properties.put("mail.pop3.host", host)
    properties.put("mail.pop3.port", port)
    // sets POP3S properties
    properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    properties.setProperty("mail.pop3.socketFactory.fallback", "false")
    properties.setProperty("mail.pop3.socketFactory.port", "995")
    properties
  }

  private def connect(folderName: String = "INBOX") = {
    emailStore.connect(userName, password)
    val emailFolder = emailStore.getFolder(folderName)
    emailFolder.open(Folder.READ_ONLY)
    emailFolder
  }

  private def findContentsFor(email: String) = {
    (for {
      message <- mailBox.getMessages
      recipient <- message.getRecipients(RecipientType.TO) if recipient.toString.contains(email)
    } yield message.getContent).map {
      case multiPart: Multipart => handleMultipart(multiPart)
      case _ => ""
    }
  }

  private def handleMultipart(multiPart: Multipart) = {
    val numberOfParts = multiPart.getCount
    (0 to numberOfParts - 1).map {
      partCount =>
        val part = multiPart.getBodyPart(partCount)
        val content = if (part.getContent != null) part.getContent.toString else ""
        content
    }.head
  }

  def getActivationUrl(toAddress: String) = {
    val contents = findContentsFor(toAddress)
    val pattern = "https://cabinet\\.plas-tek\\.ru/default\\.aspx\\?c=[0-9]*&ac=[0-9a-zA-Z]*&style=".r
    (for {
      c <- contents
      url <- pattern findFirstIn c if !url.isEmpty
    } yield url).head + "starbucks"
  }

  def close() = {
    mailBox.close(true)
    emailStore.close()
  }
}
