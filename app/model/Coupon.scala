package model

import org.joda.time.DateTime
import java.sql.Timestamp

case class Coupon(number:String, issueDate: Timestamp, expirationDate: Timestamp, isActive: Boolean, couponType: String, key: String)
