package com.example.accounts

import java.time.Instant

case class Account(
    id: Long,
    email: String,
    accessToken: String,
    expiryTime: Instant
)

object Account {
  val ID_UNASSIGNED: Long = -1
}
