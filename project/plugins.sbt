addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.2")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.7")

libraryDependencies +=
  "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.6.3"

