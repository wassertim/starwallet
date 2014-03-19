package service.common

import model._


trait Starbucks {
  def auth(userName: String, password: String): StarbucksAccount
}
