package service.sql

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import model._
import org.joda.time.DateTime

class AccountService extends common.BaseService with service.common.AccountService {
  import utility.DateTimeUtility.ts
  def get(id: Int) = database withDynSession {
    implicit val getAccount = GetResult(r => StarbucksAccount(r.<<, r.<<, getCards(id), getCoupons(id), r.<<))
    val account = sql"""
      select
        i.user_name,
        a.stars_count,
        a.sync_date
      from
        identities i
      inner join accounts a on a.identity_id = i.id
      where
        i.id = ${id};
    """.as[StarbucksAccount].firstOption
    account
  }

  private def syncCards(cards: Seq[Card], id: Int) = database withDynSession {
    sqlu"delete from cards where identity_id = ${id}".execute
    cards.foreach {
      card =>
       sqlu"""
        insert into cards(number, balance, is_active, last_transaction_date, identity_id, activation_date, last_update_date)
        values(${card.number}, ${card.balance}, ${card.isActive}, ${card.lastTransactionDate}, ${id}, ${card.activationDate}, ${ts(DateTime.now)})
       """.execute
       sqlu"delete from transactions where card_number = ${card.number}".execute
       card.transactions.foreach {
         transaction =>
           sqlu"""
           insert into transactions(card_number, date, place, type, amount, balance)
           values(${card.number}, ${transaction.date}, ${transaction.place}, ${transaction.transactionType}, ${transaction.amount}, ${transaction.balance})
           """.execute
       }
    }

  }

  def syncCoupons(coupons: Seq[Coupon], id: Int) = database withDynSession {
    sqlu"delete from coupons where identity_id = ${id};".execute
    coupons.foreach {
      coupon =>
        sqlu"""
         insert into coupons(number, identity_id, is_active, issue_date, expiration_date, type, url_key)
         values(${coupon.number}, ${id}, ${coupon.isActive}, ${coupon.issueDate}, ${coupon.expirationDate}, ${coupon.couponType}, ${coupon.key})
        """.execute
    }
  }

  def sync(account: StarbucksAccount, id: Int) = database withDynSession {
    val accountsCount = sql"select count(*) from accounts where identity_id = ${id};".as[Int].first
    if (accountsCount > 0) {
      sqlu"""
      update
        accounts
      set
        stars_count = ${account.starsCount},
        sync_date = ${account.syncDate}
    """.execute
    } else {
      sqlu"""
        insert into accounts(identity_id, stars_count, sync_date)
        values(${id}, ${account.starsCount}, ${account.syncDate})
      """.execute
    }
    syncCards(account.cards, id)
    syncCoupons(account.coupons, id)
  }

  private def getCards(id: Int): Seq[Card] = database withDynSession {
    implicit val getCardsResult = GetResult(r => Card(r.<<, r.<<, r.<<, getTransactions(r.<<)))
    sql"""
      select
        c.number,
        c.balance,
        c.is_active,
        c.number
      from
        cards c
      where
        c.identity_id = ${id}
    """.as[Card].list
  }

  private def getTransactions(cardNumber: String) = database withDynSession {
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

  private def getCoupons(id: Int) = database withDynSession {
    implicit val getCouponsResult = GetResult(r => Coupon(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        number,
        issue_date,
        expiration_date,
        is_active,
        type,
        url_key
      from
        coupons
      where
        identity_id = ${id};
    """.as[Coupon].list
  }

}
