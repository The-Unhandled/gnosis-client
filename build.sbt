version := "0.1.0-SNAPSHOT"

scalaVersion := "3.6.2"

lazy val zioVersion = "2.1.14"
lazy val zioConfigVersion = "4.0.3"
lazy val zioHttpVersion = "3.0.1"
lazy val http4sVersion = "1.0.0-M40"
lazy val tapirVersion = "1.11.11"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-http" % zioHttpVersion,
  /*"org.http4s" %% "http4s-ember-client" % http4sVersion,*/
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-logging" % "2.4.0",
  "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.33.0",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.33.0" % Provided,

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
