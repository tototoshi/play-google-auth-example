package controllers

import com.example.accounts.Account
import com.example.accounts.AccountRepository
import com.example.auth.OAuthApp
import com.example.auth.SessionKey
import jakarta.inject.Inject
import play.api.mvc.ActionRefiner
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class AuthorizedAction @Inject() (oauthApp: OAuthApp, accountRepository: AccountRepository)(implicit
    ec: ExecutionContext
) extends ActionRefiner[Request, UserRequest] {

  def executionContext = ec

  def refine[A](input: Request[A]): Future[Either[Result, UserRequest[A]]] = {
    input.session.get(SessionKey.EMAIL) match {
      case Some(email) =>
        accountRepository.findByEmail(email) match {
          case Some(account) =>
            val request = UserRequest(
              account,
              input
            )

            if (request.isAccessTokenExpired)
              Future.successful(Left(Redirect(routes.OAuthController.login)))
            else Future.successful(Right(request))
          case None => Future.successful(Left(Redirect(routes.OAuthController.login)))
        }

      case None => Future.successful(Left(Redirect(routes.OAuthController.login)))
    }
  }
}
