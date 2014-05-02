package utility

import com.codahale.jerkson.Json

object JsonResults {
  def json[C](content: C) = play.api.mvc.Results.Ok(Json.generate[C](content)).withHeaders("Content-Type" -> "application/json")
}
