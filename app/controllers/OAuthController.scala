package controllers

import com.example.accounts.Account
import com.example.accounts.AccountRepository
import com.example.auth.OAuthApp
import com.example.auth.SessionKey
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.RequestHeader

import java.time.Instant
import scala.jdk.CollectionConverters._

@Singleton
class OAuthController @Inject() (
    cc: ControllerComponents,
    oauthApp: OAuthApp,
    accountRepository: AccountRepository
) extends AbstractController(cc) {

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login())
  }

  def startAuthorization: Action[AnyContent] = Action { implicit request =>
    // https://developers.google.com/identity/protocols/oauth2/scopes
    val scopes = List(
      "https://www.googleapis.com/auth/userinfo.email"
    ).asJava

    val url = new GoogleAuthorizationCodeRequestUrl(
      oauthApp.clientId,
      oauthApp.redirectUri,
      scopes
    ).build()

    Redirect(url)
  }

  def authorize: Action[AnyContent] = Action { implicit request: RequestHeader =>
    val scheme = if (request.secure) "https://" else "http://"
    val fullUrl = scheme + request.host + request.uri
    val authorizationCodeResponseUrl = new AuthorizationCodeResponseUrl(fullUrl)
    val code = authorizationCodeResponseUrl.getCode()

    val transport = new NetHttpTransport()
    val jsonFactory = new GsonFactory()

    val googleAuthorizationCodeTokenRequest = new GoogleAuthorizationCodeTokenRequest(
      transport,
      jsonFactory,
      oauthApp.clientId,
      oauthApp.clientSecret,
      code,
      oauthApp.redirectUri
    )
    val googleAuthorizationCodeTokenResponse = googleAuthorizationCodeTokenRequest.execute()

    val idToken =
      GoogleIdToken.parse(jsonFactory, googleAuthorizationCodeTokenResponse.getIdToken())

    val payload = idToken.getPayload()

    val email = payload.getEmail()
    val accessToken = googleAuthorizationCodeTokenResponse.getAccessToken()
    val expiresInSeconds = googleAuthorizationCodeTokenResponse.getExpiresInSeconds()
    val expiryTime = Instant.now().plusSeconds(expiresInSeconds)

    val accountId = accountRepository.findByEmail(email).map(_.id).getOrElse(Account.ID_UNASSIGNED)

    val googleAccount = Account(
      accountId,
      email,
      accessToken,
      expiryTime
    )

    accountRepository.save(googleAccount)

    Redirect(routes.HomeController.index)
      .withSession(SessionKey.EMAIL -> email)
  }

  def logout: Action[AnyContent] = Action { implicit request =>
    Redirect(routes.HomeController.index).withNewSession
  }

}
