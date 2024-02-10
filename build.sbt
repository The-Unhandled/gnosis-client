version := "0.1.0-SNAPSHOT"

scalaVersion := "3.3.1"

lazy val zioVersion = "2.1-RC1"
lazy val zioConfigVersion = "4.0.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-http" % "3.0.0-RC4",
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-logging" % "2.2.0",
// Test
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "gnosis-client",
    idePackagePrefix := Some("xyz.forsaken.gnosisclient")
  )
