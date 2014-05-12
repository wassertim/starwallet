package service.fake.pop

class EmailClient(login: String, password: String) extends service.common.pop.EmailClient {
  def getActivationUrl(toAddress: String) = "http://activation.url"

  def close() = {}
}
