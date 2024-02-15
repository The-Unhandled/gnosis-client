version := "0.1.0-SNAPSHOT"

scalaVersion := "3.3.1"

lazy val zioVersion = "2.1-RC1"
lazy val zioConfigVersion = "4.0.1"

// ZIO GRPC
/*PB.targets in Compile := Seq(
  scalapb.gen(grpc = true) -> (sourceManaged in Compile).value / "scalapb",
  scalapb.zio_grpc.ZioCodeGenerator -> (sourceManaged in Compile).value / "scalapb"
)*/

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-http" % "3.0.0-RC4",
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-logging" % "2.2.0",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % "2.28.2",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.28.2" % Provided,
  /*"io.grpc" % "grpc-netty" % "1.50.1",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,*/

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
