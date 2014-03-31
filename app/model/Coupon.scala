package model

import org.joda.time.DateTime

case class Coupon(number:String, issueDate: DateTime, expirationDate: DateTime, isActive: Boolean, couponType: String, key: String)
