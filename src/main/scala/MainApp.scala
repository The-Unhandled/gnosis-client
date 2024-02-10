package xyz.forsaken.gnosisclient

import zio.*
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.logging.consoleLogger

/** @author
  *   Petros Siatos
  */
object MainApp extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(
      TypesafeConfigProvider
        .fromResourcePath()
    ) ++
      Runtime.removeDefaultLoggers >>> consoleLogger()

  val textRoute = Method.GET / "text" -> handler(Response.text("Hello World!"))

  val routes = Routes(
    textRoute
  )

  val app: HttpApp[Any] = routes.toHttpApp

  val serverConfig: Layer[Config.Error, Server.Config] =
    ZLayer.fromZIO(ZIO.config[GnosisConfig](GnosisConfig.config).map { c =>
      Server.Config.default.port(c.port)
    })

  def run =
    (Server
      .install(app)
      .flatMap(port =>
        ZIO.logInfo(s"Started server on port: $port")
      ) *> ZIO.never)
      .provide(
        serverConfig,
        Server.live
      )
