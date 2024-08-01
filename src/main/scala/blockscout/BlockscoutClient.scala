package xyz.forsaken.gnosisclient
package blockscout

import gnosis.{AccountsClient, xDai}

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.*
import zio.http.*

import javax.naming.ConfigurationException

final class BlockscoutClient(
    val config: BlockscoutConfig,
    httpClient: Client
) extends AccountsClient:

  final case class AccountResponse(coinBalance: xDai)
  
  object AccountResponse:
    given xdaiCodec: JsonValueCodec[AccountResponse] = JsonCodecMaker.make(CodecMakerConfig.withFieldNameMapper(JsonCodecMaker.enforce_snake_case))

  override def getxDaiBalance(address: String): Task[xDai] =

    // FIXME:
    val url: URL = URL
      .fromURI(config.url)
      .getOrElse(throw new ConfigurationException("Invalid URL"))
      ./("addresses")
      ./(address)
      .queryParams(QueryParams("apikey" -> config.apiKey))

    (for
      response <- httpClient.url(url).get("/")
      responseBody <- response.body.asString
      coinBalance <- ZIO
        .attempt(
          readFromString[AccountResponse](responseBody)
        )
        .map(_.coinBalance)
    yield coinBalance).provideSomeLayer(Scope.default)

object BlockscoutClient:
  val layer: ZLayer[Client, Config.Error, BlockscoutClient] =
    ZLayer {
      for
        config <- ZIO.config(BlockscoutConfig.config)
        client <- ZIO.service[Client]
      yield BlockscoutClient(config, client)
    }
