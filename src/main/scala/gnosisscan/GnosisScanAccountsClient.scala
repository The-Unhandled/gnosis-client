package xyz.forsaken.gnosisclient
package gnosisscan

import gnosis.xDai

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.*
import zio.http.*

import javax.naming.ConfigurationException

final class GnosisScanAccountsClient(
    config: GnosisScanConfig,
    httpClient: Client
) extends AccountsClient:

  final case class BalanceResponse(
      status: String,
      message: String,
      result: String
  )

  object BalanceResponse:
    given codec: JsonValueCodec[BalanceResponse] = JsonCodecMaker.make

  private val QUERY_PARAMS = QueryParams(
    "module" -> "account",
    "action" -> "balance",
    "tag" -> "latest",
    "apikey" -> config.apiKey
  )

  override def getxDaiBalance(address: String): Task[xDai] =

    // FIXME: 
    val url: URL = URL
      .fromURI(config.url)
      .getOrElse(throw new ConfigurationException("Invalid URL"))
      .queryParams(QUERY_PARAMS ++ QueryParams("address" -> address))

    (for
      response <- httpClient.url(url).get("/")
      responseBody <- response.body.asString
      xDai <- ZIO
        .attempt(
          readFromString[BalanceResponse](responseBody)
        )
        .map(x => xDai.fromWei(BigDecimal(x.result)))
    yield xDai).provideSomeLayer(Scope.default)

object GnosisScanAccountsClient:
  val layer: ZLayer[Client, Config.Error, GnosisScanAccountsClient] =
    ZLayer {
      for
        config <- ZIO.config(GnosisScanConfig.config)
        client <- ZIO.service[Client]
      yield GnosisScanAccountsClient(config, client)
    }
