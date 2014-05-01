package utility

import com.codahale.jerkson.Json

object JsonResults {
  def ok[C](content: C) = play.api.mvc.Results.Ok(Json.generate[C](content))
}
