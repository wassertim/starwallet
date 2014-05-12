package service.common.email

trait EmailClient {
  def getActivationUrl(toAddress: String):String
  def close()
}
