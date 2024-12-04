val _scalaVersion = "3.4.3"

inThisBuild(
  List(
    scalaVersion := _scalaVersion,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

lazy val `play-google-auth-example` = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-google-auth-example",
    organization := "com.github.tototoshi",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := _scalaVersion,
    libraryDependencies ++= Seq(
      guice,
      "com.google.api-client" % "google-api-client" % "1.32.1",
      "com.google.apis" % "google-api-services-sheets" % "v4-rev20210629-1.32.1",
      "com.h2database" % "h2" % "2.1.214",
      "org.scalikejdbc" %% "scalikejdbc" % "4.3.2",
      "org.scalikejdbc" %% "scalikejdbc-config" % "4.3.2",
      "org.flywaydb" %% "flyway-play" % "9.1.0",
      "org.scalatest" %% "scalatest" % "3.2.19" % "test"
    ),
    scalacOptions ++= Seq(
      "-Wunused:imports",
      s"-Wconf:src=target/scala-${scalaVersion.value}/routes/main/*:s",
      s"-Wconf:src=target/scala-${scalaVersion.value}/twirl/*:s"
    )
  )
