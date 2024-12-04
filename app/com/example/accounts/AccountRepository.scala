package com.example.accounts

import jakarta.inject.Inject
import scalikejdbc._

class AccountRepository @Inject() (connectionPool: ConnectionPool) {

  private def db: DB = DB(connectionPool.borrow())

  def findByEmail(email: String): Option[Account] = {
    db.readOnly { implicit session =>
      selectByEmail(email)
    }
  }

  def save(account: Account): Unit = {
    db.localTx { implicit session =>
      selectByEmail(account.email)(using session) match {
        case Some(a) => update(account)
        case None => insert(account)
      }
    }
  }

  private def *(rs: WrappedResultSet): Account =
    Account(
      rs.long("id"),
      rs.string("email"),
      rs.string("access_token"),
      rs.zonedDateTime("expiry_time").toInstant()
    )

  private def insert(account: Account)(using DBSession): Unit = {
    sql"""insert into accounts(email, access_token, expiry_time)
              values (${account.email}, ${account.accessToken}, ${account.expiryTime})""".update
      .apply()
  }

  private def update(account: Account)(using DBSession): Unit = {
    sql"""update accounts
              set email = ${account.email}, 
                  access_token = ${account.accessToken},
                  expiry_time = ${account.expiryTime}
              where id = ${account.id}
            """.update
      .apply()
  }

  private def selectByEmail(email: String)(using DBSession): Option[Account] = {
    sql"""select id, email, access_token, expiry_time from accounts where email = ${email}"""
      .map(*)
      .single
      .apply()
  }

}
