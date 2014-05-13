package model

import java.sql.Timestamp

case class IdentityListItem(id: Int, userName: String, starsCount: Int, activeCouponsCount: Int, lastUpdate: Timestamp, isActive: Boolean)
