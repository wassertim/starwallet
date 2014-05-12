package service.fake.pop

/**
 * Created by tim on 12/05/14.
 */
class EmailClient(login: String, password: String) extends service.common.pop.EmailClient {
  override def getActivationUrl(toAddress: String): String = ???

  override def close(): Unit = ???
}
