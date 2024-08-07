version := "0.1.0-SNAPSHOT"

scalaVersion := "3.4.2"

lazy val zioVersion = "2.1.7"
lazy val zioConfigVersion = "4.0.2"
lazy val zioHttpVersion = "3.0.0-RC9"
lazy val http4sVersion = "1.0.0-M40"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-http" % zioHttpVersion,
  /*"org.http4s" %% "http4s-ember-client" % http4sVersion,*/
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-logging" % "2.3.0",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.28.2",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.28.2" % Provided,
  "org.bouncycastle" % "bcprov-jdk18on" % "1.77",

// Test
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-http-testkit" % zioHttpVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "gnosis-client",
    idePackagePrefix := Some("xyz.forsaken.gnosisclient")
  )
