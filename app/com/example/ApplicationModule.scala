package com.example

import com.example.auth.OAuthApp
import com.example.auth.OAuthAppProvider
import com.example.database.H2ConnectionPoolProvider
import com.google.inject.AbstractModule
import scalikejdbc.ConnectionPool

class ApplicationModule extends AbstractModule {
  override def configure() = {
    bind(classOf[ConnectionPool])
      .toProvider(classOf[H2ConnectionPoolProvider])
      .asEagerSingleton()
    bind(classOf[OAuthApp])
      .toProvider(classOf[OAuthAppProvider])
  }
}
