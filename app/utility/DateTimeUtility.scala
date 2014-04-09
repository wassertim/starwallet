package utility

import org.joda.time.DateTime
import java.sql.Timestamp

object DateTimeUtility {
  def ts(date: DateTime): Timestamp = new Timestamp(date.toDate.getTime)
}
