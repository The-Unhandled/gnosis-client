version := "0.1.0-SNAPSHOT"

scalaVersion := "3.6.2"

lazy val zioVersion = "2.1.16"
lazy val zioConfigVersion = "4.0.3"
lazy val zioHttpVersion = "3.0.1"
lazy val sttpVersion = "4.0.0-RC1"
lazy val tapirVersion = "1.11.15"
lazy val jsoniterVersion = "2.33.2"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-http" % zioHttpVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-logging" % "2.5.0",
  "dev.zio" %% "zio-kafka" % "2.11.0",
  "com.softwaremill.sttp.client4" %% "zio" % sttpVersion,
  "com.softwaremill.sttp.client4" %% "core" % sttpVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % jsoniterVersion,
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsoniterVersion % Provided,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "io.grpc" % "grpc-netty" % "1.71.0",

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

Compile / PB.targets := Seq(
  scalapb.gen(grpc = true) -> (Compile / sourceManaged).value / "scalapb",
  scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value / "scalapb"
)

Compile / scalacOptions ++= Seq(
  "-Wconf:src=src_managed/.*:s" // ✅ Silences warnings in generated files
)
