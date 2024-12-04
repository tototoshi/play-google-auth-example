package controllers

import com.example.accounts.Account
import play.api.mvc.Request
import play.api.mvc.WrappedRequest

import java.time.Instant

case class UserRequest[A](
    account: Account,
    request: Request[A]
) extends WrappedRequest[A](request) {
  def isAccessTokenExpired: Boolean =
    account.expiryTime.isBefore(Instant.now())
}
