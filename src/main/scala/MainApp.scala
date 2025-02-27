package xyz.forsaken.gnosisclient

import aura.{AuraService, AuraServiceImpl}
import beaconcha.*
import blockscout.*
import gnosisscan.*
import grpc.aura.AuraGrpcClientImpl
import kafka.*
import server.*
import slack.SlackClientLayer

import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.*
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.logging.{ConsoleLoggerConfig, LogFilter, LogFormat, consoleLogger}

/** @author
  *   Petros Siatos
  */
object MainApp extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> Runtime.setConfigProvider(
      TypesafeConfigProvider
        .fromResourcePath()
    ) >>> consoleLogger(ConsoleLoggerConfig(LogFormat.colored, LogFilter.LogLevelByNameConfig.default))

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
      kafkaConsumer <- ZIO.service[KafkaConsumer]
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
      _ <- kafkaConsumer.runConsumer
      _ <- Server.serve(routes ++ swaggerRoutes)
    yield ())
      .provide(
        serverConfig,
        KafkaConsumer.layer,
        BalanceEndpointImpl.layer,
        ContractEndpointImpl.layer,
        ValidatorEndpointImpl.layer,
        BlockscoutClient.layer,
        BeaconchaClient.layer,
        // GnosisScanAccountsClient.layer,
        GnosisScanContractsClient.layer,
        GnosisScanGethProxyClient.layer,
        AuraServiceImpl.layer,
        AuraGrpcClientImpl.layer,
        SlackClientLayer.layer,
        Client.default,
        Server.live
      )
