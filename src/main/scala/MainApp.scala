package xyz.forsaken.gnosisclient

import beaconcha.*
import blockscout.*
import gnosisscan.*
import server.*
import slack.SlackClientLayer

import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.logging.consoleLogger
import sttp.tapir.swagger.bundle.SwaggerInterpreter

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
        .port(c.port)
    })
  
  def run =
    (for
      balanceEndpoint <- ZIO.service[BalanceEndpoint]
      contractEndpoint <- ZIO.service[ContractEndpoint]
      validatorEndpoint <- ZIO.service[ValidatorEndpoint]
      routes = Routes(
        textRoute
      ) ++ balanceEndpoint.routes ++ contractEndpoint.routes ++ validatorEndpoint.routes
      swaggerEndpoints = SwaggerInterpreter().fromEndpoints[Task](
          balanceEndpoint.endpoints ++
          contractEndpoint.endpoints ++
          validatorEndpoint.endpoints,
        "Gnosis Client API",
        "1.0")
      swaggerRoutes = ZioHttpInterpreter().toHttp(swaggerEndpoints)
      _ <- Server.serve(routes ++ swaggerRoutes)
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
