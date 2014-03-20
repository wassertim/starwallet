package service.common

import model._


trait Starbucks {
  def getAccountData(authInfo: AuthInfo): StarbucksAccount
}
