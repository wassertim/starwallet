package service.sql

import service.sql.common.BaseService

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import model._

class CardService extends BaseService with service.common.CardService {
  def listByUser(userId: Int) = database withDynSession {
    implicit val getGardListResult = GetResult(r => CardListItem(r.<<, r.<<, r.<<, r.<<, r.<<, AuthInfo(r.<<, r.<<, "")))
    sql"""
      select
        c.number,
        c.balance,
        c.is_active,
        c.last_transaction_date,
        c.activation_date,
        i.id,
        i.user_name
      from identities i
      inner join cards c on i.id = c.identity_id
      where
        i.user_id = ${userId}
      order by c.last_transaction_date desc;
    """.as[CardListItem].list()
  }

  def get(number: String, userId: Int) = database withDynSession {
    implicit val getCardResult = GetResult(r => Card(r.<<, r.<<, r.<<, r.<<, getTransactions(number)))
    val card = sql"""
      select
        c.number,
        c.balance,
        c.is_active,
        pc.pin_code
      from
        cards c
      inner join identities i on i.id = c.identity_id
      left join pin_codes pc on pc.card_number = c.number
      where
        number = ${number} and i.user_id = ${userId};
    """.as[Card].firstOption
    card
  }

  def listByIdentity(id: Int) = database withDynSession {
    implicit val getCardsResult = GetResult(r => CardListItem(r.<<, r.<<, r.<<, r.<<, r.<<, AuthInfo(id, "", "")))
    sql"""
      select
        c.number,
        c.balance,
        c.is_active,
        c.last_transaction_date,
        c.activation_date
      from
        cards c
      where
        c.identity_id = ${id}
    """.as[CardListItem].list
  }

  def getTransactions(cardNumber: String) = database withDynSession {
    implicit val getTransactionResult = GetResult(r => Transaction(r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        date,
        place,
        type,
        amount,
        balance
      from
        transactions
      where
        card_number = ${cardNumber};
    """.as[Transaction].list
  }

  def savePin(pinCode: String, number: String) = database withDynSession {
    sqlu"delete from pin_codes where card_number = ${number};".execute
    sqlu"insert into pin_codes(card_number, pin_code) values(${number}, ${pinCode});".execute
  }
}
