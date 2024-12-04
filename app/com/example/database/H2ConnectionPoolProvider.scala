package com.example.database
import jakarta.inject.Inject
import org.h2.jdbcx.JdbcDataSource
import play.api.Configuration
import scalikejdbc.ConnectionPool
import scalikejdbc.DataSourceConnectionPool

import javax.inject.Provider

class H2ConnectionPoolProvider @Inject() (
    configuration: Configuration
) extends Provider[ConnectionPool] {

  def get(): ConnectionPool = {
    val driver = configuration.get[String]("db.default.driver")
    val url = configuration.get[String]("db.default.url")
    val username = configuration.get[String]("db.default.username")
    val password = configuration.get[String]("db.default.password")

    Class.forName(driver)

    val ds = new JdbcDataSource();
    ds.setUrl(url)
    ds.setUser(username)
    ds.setPassword(password)
    new DataSourceConnectionPool(ds)
  }

}
