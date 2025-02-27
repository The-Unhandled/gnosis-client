package xyz.forsaken.gnosisclient
package binance

import domain.Price
import infra.CommonHttpClient

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.*
import zio.http.*

import java.time.Instant

/**
 * @see [[https://developers.binance.com/docs/binance-spot-api-docs/faqs/market_data_only Binance Market Data API]]
 */
final class BinanceClient(
    val config: BinanceConfig,
    httpClient: Client
) extends CommonHttpClient:

  import BinanceClient.*

  final val uri = config.url
  final val apiKey = config.apiKey

  def getPrice(pair: String): Task[Price] =
    (for
      url <- getUrl
      createurl = url
        ./("avgPrice") 
        .removeQueryParam("apikey")
        .addQueryParam("symbol", pair)
      response <- request[BinancePriceResponse](createurl)
    yield response.price).provide(ZLayer.succeed(httpClient), Scope.default)

object BinanceClient:

  case class BinancePriceResponse(mins: Int, price: Price, closeTime: Instant)

  object BinancePriceResponse:
    given codec: JsonValueCodec[BinancePriceResponse] = JsonCodecMaker.make
  
    given instantCodec: JsonValueCodec[Instant] = new JsonValueCodec[Instant] {
      override def decodeValue(in: JsonReader, default: Instant): Instant =
        Instant.ofEpochMilli(in.readLong())
  
      override def encodeValue(x: Instant, out: JsonWriter): Unit =
        out.writeVal(x.toEpochMilli)
  
      override def nullValue: Instant = Instant.EPOCH
    }

  val layer: ZLayer[Client, Config.Error, BinanceClient] =
    ZLayer {
      for
        config <- ZIO.config(BinanceConfig.config)
        client <- ZIO.service[Client]
      yield BinanceClient(config, client)
    }
