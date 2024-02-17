package xyz.forsaken.gnosisclient

import gnosisscan.{AccountsClient, BalanceEndpoint, BalanceEndpointLayer, GnosisScanAccountsClient}

import xyz.forsaken.gnosisclient.slack.SlackClientLayer
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

  val textRoute = Method.GET / "hello" -> handler(Response.text("Hello World!"))

  val serverConfig: Layer[Config.Error, Server.Config] =
    ZLayer.fromZIO(ZIO.config[ServerConfig](ServerConfig.config).map { c =>
      Server.Config.default.port(c.port)
    })

  def errorHandler: Throwable => Response = { case e =>
    Response.text(e.getMessage).status(Status.InternalServerError)
  }

  def run =
    (for
      balanceEndpoint <- ZIO.service[BalanceEndpoint]
      routes = Routes(textRoute) ++ balanceEndpoint.routes
      app = routes.handleError(errorHandler).toHttpApp
      // Fixme: install then log
      port <- Server.serve(app)
      _ <- ZIO.logInfo(s"Started server")
    yield ())
      .provide(
        serverConfig,
        BalanceEndpointLayer.layer,
        GnosisScanAccountsClient.layer,
        SlackClientLayer.layer,
        Client.default,
        Server.live
      )
