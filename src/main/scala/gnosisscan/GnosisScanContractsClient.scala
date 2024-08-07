package xyz.forsaken.gnosisclient
package gnosisscan

import gnosisscan.GnosisScanClient.Module.Contract

import zio.*
import zio.http.*

final class GnosisScanContractsClient(
    val config: GnosisScanConfig,
    httpClient: Client
) extends GnosisScanClient
    with ContractsClient:

  final protected val module = Contract

  override def getAbi(address: String): Task[String] =
    (for
      url <- getUrl("getabi")
      createurl = url
        .addQueryParams(QueryParams("address" -> address))
      abi <- request[ResponseBody](createurl)
        .map(_.result)
    yield abi).provide(ZLayer.succeed(httpClient), Scope.default)

object GnosisScanContractsClient:
  val layer: ZLayer[Client, Config.Error, GnosisScanContractsClient] =
    ZLayer {
      for
        config <- ZIO.config(GnosisScanConfig.config)
        client <- ZIO.service[Client]
      yield GnosisScanContractsClient(config, client)
    }
