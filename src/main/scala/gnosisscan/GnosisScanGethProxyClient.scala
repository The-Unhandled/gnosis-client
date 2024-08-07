package xyz.forsaken.gnosisclient
package gnosisscan

import domain.Tokens.ERC20Token
import domain.gnosis.xDai
import gnosisscan.GnosisScanClient.Module.Proxy

import com.github.plokhotnyuk.jsoniter_scala.core.*
import zio.*
import zio.http.*

import javax.naming.ConfigurationException

final class GnosisScanGethProxyClient(
    val config: GnosisScanConfig,
    httpClient: Client
) extends GnosisScanClient
    with GethProxyClient:

  final protected val module = Proxy

  override def getBalance(erc20Token: ERC20Token, address: String): Task[xDai] =

    // FIXME:
    val url: URL = URL
      .fromURI(config.url)
      .getOrElse(throw new ConfigurationException("Invalid URL"))
      .addQueryParams(
        queryParams("eth_call") ++ QueryParams(
          "to" -> erc20Token.address
        ) ++ QueryParams("data" -> address)
      )

    println(s"URL: $url")

    (for
      response <- httpClient.url(url).get("/")
      responseBody <- response.body.asString
      _ = println(s"Got Response: $response")
      _ = println(s"The Body: $responseBody")
      _ <- ZIO.logInfo(s"ZIO Got Response: $response")
      wxDai <- ZIO
        .attempt(
          readFromString[JsonRpcResponseBody](responseBody)
        )
        // .tap(responseBody => ZIO.logInfo(s"Got response $responseBody"))
        .map(responseBody => xDai.fromHexadecimal(responseBody.result))
    yield wxDai).provideSomeLayer(Scope.default)

object GnosisScanGethProxyClient:
  val layer: ZLayer[Client, Config.Error, GnosisScanGethProxyClient] =
    ZLayer {
      for
        config <- ZIO.config(GnosisScanConfig.config)
        client <- ZIO.service[Client]
      yield GnosisScanGethProxyClient(config, client)
    }
