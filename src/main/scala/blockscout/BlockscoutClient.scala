package xyz.forsaken.gnosisclient
package blockscout

import domain.*
import domain.Tokens.ERC20Token
import domain.gnosis.xDai

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.*
import zio.http.*

import javax.naming.ConfigurationException

final class BlockscoutClient(
    val config: BlockscoutConfig,
    httpClient: Client
) extends AccountsClient
    with TokensClient:

  import BlockscoutClient.*

  final case class AccountResponse(coinBalance: xDai)

  object AccountResponse:
    given xdaiCodec: JsonValueCodec[AccountResponse] = JsonCodecMaker.make(
      CodecMakerConfig.withFieldNameMapper(JsonCodecMaker.enforce_snake_case)
    )

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

  override def getTokenBalances(address: String): Task[Set[TokenBalance]] =
    // FIXME:
    val url: URL = URL
      .fromURI(config.url)
      .getOrElse(throw new ConfigurationException("Invalid URL"))
      ./("addresses")
      ./(address)
      ./("token-balances")
      .queryParams(QueryParams("apikey" -> config.apiKey))

    (for
      response <- httpClient.url(url).get("/")
      responseBody <- response.body.asString
      tokenBalancesResponse <- ZIO
        .attempt(
          readFromString[Set[TokenBalancesResponse]](responseBody)
        )
      tokenBalances = tokenBalancesResponse.flatMap(r => r.token.toERC20Token.map(erc20 => TokenBalance(erc20, r.value)))
    yield tokenBalances).provideSomeLayer(Scope.default)

object BlockscoutClient:

  case class TokenResponse(name: Option[String], symbol: Option[String], address: String):
    def toERC20Token: Option[ERC20Token] = symbol.flatMap(ERC20Token(_, address))

  case class TokenBalancesResponse(token: TokenResponse, value: Balance)

  object TokenBalancesResponse:

    given codec: JsonValueCodec[Set[TokenBalancesResponse]] = JsonCodecMaker.make

  val layer: ZLayer[Client, Config.Error, BlockscoutClient] =
    ZLayer {
      for
        config <- ZIO.config(BlockscoutConfig.config)
        client <- ZIO.service[Client]
      yield BlockscoutClient(config, client)
    }
