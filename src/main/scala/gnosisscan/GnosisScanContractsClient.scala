package xyz.forsaken.gnosisclient
package gnosisscan

import gnosisscan.GnosisScanClient.Module.Contract

import com.github.plokhotnyuk.jsoniter_scala.core.*
import zio.*
import zio.http.*

import javax.naming.ConfigurationException

final class GnosisScanContractsClient(
    val config: GnosisScanConfig,
    httpClient: Client
) extends GnosisScanClient
    with ContractsClient:

  final protected val module = Contract

  override def getAbi(address: String): Task[String] =

    // FIXME:
    val url: URL = URL
      .fromURI(config.url)
      .getOrElse(throw new ConfigurationException("Invalid URL"))
      .addQueryParams(queryParams("getabi") ++ QueryParams("address" -> address))

    (for
      response <- httpClient.url(url).get("/")
      responseBody <- response.body.asString
      abi <- ZIO
        .attempt(
          readFromString[ResponseBody](responseBody)
        )
        .map(_.result)
    yield abi).provideSomeLayer(Scope.default)

object GnosisScanContractsClient:
  val layer: ZLayer[Client, Config.Error, GnosisScanContractsClient] =
    ZLayer {
      for
        config <- ZIO.config(GnosisScanConfig.config)
        client <- ZIO.service[Client]
      yield GnosisScanContractsClient(config, client)
    }
