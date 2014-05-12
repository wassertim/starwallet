package service.common.pop

trait EmailClient {
  def getActivationUrl(toAddress: String):String
  def close()
}
