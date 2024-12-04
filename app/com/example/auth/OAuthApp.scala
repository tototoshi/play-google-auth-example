package com.example.auth

import com.google.inject.Provider
import jakarta.inject.Inject
import play.api.Configuration

case class OAuthApp(
    clientId: String,
    clientSecret: String,
    redirectUri: String
)

class OAuthAppProvider @Inject() (configuration: Configuration) extends Provider[OAuthApp] {
  def get(): OAuthApp = OAuthApp(
    configuration.get[String]("oauth.clientId"),
    configuration.get[String]("oauth.clientSecret"),
    configuration.get[String]("oauth.redirectUri")
  )
}
