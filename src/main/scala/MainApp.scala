package xyz.forsaken.gnosisclient

import beaconcha.*
import blockscout.*
import gnosisscan.*
import server.*
import slack.SlackClientLayer

import zio.*
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.logging.consoleLogger

/** @author
  *   Petros Siatos
  */
object MainApp extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> Runtime.setConfigProvider(
      TypesafeConfigProvider
        .fromResourcePath()
    ) >>> consoleLogger()

  private val textRoute =
    Method.GET / "hello" -> handler(Response.text("Hello World!"))

  private val serverConfig: Layer[Config.Error, Server.Config] =
    ZLayer.fromZIO(ZIO.config[ServerConfig](ServerConfig.config).map { c =>
      Server.Config.default
        .idleTimeout(30.seconds)
        .port(c.port)
    })

  private def errorHandler: Throwable => UIO[Response] =
    e =>
      for {
        _ <- ZIO.logErrorCause("Error", Cause.fail(e))
        response = Response.text("OOPS").status(Status.InternalServerError)
      } yield response

  def run =
    (for
      balanceEndpoint <- ZIO.service[BalanceEndpoint]
      contractEndpoint <- ZIO.service[ContractEndpoint]
      validatorEndpoint <- ZIO.service[ValidatorEndpoint]
      routes = Routes(
        textRoute
      ) ++ balanceEndpoint.routes ++ contractEndpoint.routes ++ validatorEndpoint.routes
      routesWithoutError: Routes[Any, Response] = routes.handleErrorZIO(
        errorHandler
      )
      _ <- Server.serve(routesWithoutError)
    yield ())
      .provide(
        serverConfig,
        BalanceEndpointImpl.layer,
        ContractEndpointImpl.layer,
        ValidatorEndpointImpl.layer,
        BlockscoutClient.layer,
        BeaconchaClient.layer,
        // GnosisScanAccountsClient.layer,
        GnosisScanContractsClient.layer,
        GnosisScanGethProxyClient.layer,
        SlackClientLayer.layer,
        Client.default,
        Server.live
      )
