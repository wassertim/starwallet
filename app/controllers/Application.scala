package controllers

import play.api.mvc._
import com.google.inject.Inject
import model.AuthInfo

class Application @Inject()(val starbucks: service.common.Starbucks) extends Controller {

  def index = Action {
    val accounts = (1 to 27).map { i =>
      starbucks.getAccountData(AuthInfo(s"test$i", "test"))
    }
    val activeCouponst = accounts.flatMap(s => s.couponList.filter(c => c.status == "Активен"))
    Ok(views.html.index("Your new application is ready."))
  }
}