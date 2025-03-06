package xyz.forsaken.gnosisclient
package cowswap

import zio.*
import zio.http.*
import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import infra.CommonHttpClient

final class CowSwapClient(val config: CowSwapConfig, httpClient: Client)
    extends CommonHttpClient:

  import CowSwapClient._

  final val uri = config.url
  final val apiKey = config.apiKey

  def getQuote(requestBody: CowSwapQuoteRequestBody): Task[CowSwapQuoteAmount] =
    (for
      baseUrl <- getUrl // inherited from CommonHttpClient, returns a URL
      body = Body.fromString(writeToString(requestBody))
      createUrl = baseUrl./("api/v1/quote")
      response <- request[CowSwapQuoteResponse](createUrl, Some(body))
    yield response.quote.buyAmount).provide(ZLayer.succeed(httpClient), Scope.default)

object CowSwapClient:

  final case class CowSwapQuoteRequestBody(
                                     sellToken: String,
                                     buyToken: String,
                                     from: String,
                                     receiver: String,
                                     appData: String,
                                     appDataHash: String,
                                     partiallyFillable: Boolean,
                                     priceQuality: String,
                                     validFor: Int,
                                     kind: String,
                                     sellAmountBeforeFee: String
                                   )

  object CowSwapQuoteRequestBody:
    given JsonValueCodec[CowSwapQuoteRequestBody] = JsonCodecMaker.make

  final case class CowSwapQuoteResponse(quote: Quote)

  final case class Quote(buyAmount: CowSwapQuoteAmount)

  object CowSwapQuoteResponse:
    given JsonValueCodec[CowSwapQuoteResponse] = JsonCodecMaker.make

  object Quote:
    given JsonValueCodec[Quote] = JsonCodecMaker.make
    
  val layer: ZLayer[Client, Config.Error, CowSwapClient] =
    ZLayer {
      for
        config <- ZIO.config(CowSwapConfig.config)
        client <- ZIO.service[Client]
      yield CowSwapClient(config, client)
    }
