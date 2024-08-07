package xyz.forsaken.gnosisclient
package gnosisscan

import domain.Tokens.ERC20Token
import domain.gnosis.xDai
import gnosisscan.GnosisScanClient.Module.Proxy

import zio.*
import zio.http.*

final class GnosisScanGethProxyClient(
    val config: GnosisScanConfig,
    httpClient: Client
) extends GnosisScanClient
    with GethProxyClient:

  final protected val module = Proxy

  override def getBalance(erc20Token: ERC20Token, address: String): Task[xDai] =
    (for
      url <- getUrl("eth_call")
      createurl = url
        .addQueryParams(
          QueryParams("to" -> erc20Token.address, "data" -> address)
        )
      wxDai <- request[JsonRpcResponseBody](createurl)
        .map(responseBody => xDai.fromHexadecimal(responseBody.result))
    yield wxDai).provide(ZLayer.succeed(httpClient), Scope.default)

object GnosisScanGethProxyClient:
  val layer: ZLayer[Client, Config.Error, GnosisScanGethProxyClient] =
    ZLayer {
      for
        config <- ZIO.config(GnosisScanConfig.config)
        client <- ZIO.service[Client]
      yield GnosisScanGethProxyClient(config, client)
    }
