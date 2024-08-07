package xyz.forsaken.gnosisclient
package blockscout

import domain.*
import domain.Tokens.ERC20Token
import domain.gnosis.xDai
import infra.CommonHttpClient

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.*
import zio.http.*

final class BlockscoutClient(
    val config: BlockscoutConfig,
    httpClient: Client
) extends CommonHttpClient
    with AccountsClient
    with TokensClient:

  import BlockscoutClient.*

  final val apiKey = config.apiKey
  final val uri = config.url

  override def getxDaiBalance(address: String): Task[xDai] =
    (for
      url <- getUrl
      createurl = url
        ./("addresses")
        ./(address)
      response <- request[AccountResponse](createurl)
    yield response.coinBalance)
      .provide(ZLayer.succeed(httpClient), Scope.default)

  override def getTokenBalances(address: String): Task[Set[TokenBalance]] =
    (for
      url <- getUrl
      createurl = url
        ./("addresses")
        ./(address)
        ./("token-balances")
      tokenBalancesResponse <- request[Set[TokenBalancesResponse]](createurl)
      tokenBalances = tokenBalancesResponse.flatMap(r =>
        r.token.toERC20Token.map(erc20 => TokenBalance(erc20, r.value))
      )
    yield tokenBalances).provide(ZLayer.succeed(httpClient), Scope.default)

object BlockscoutClient:

  final case class AccountResponse(coinBalance: xDai)

  object AccountResponse:
    given xdaiCodec: JsonValueCodec[AccountResponse] = JsonCodecMaker.make(
      CodecMakerConfig.withFieldNameMapper(JsonCodecMaker.enforce_snake_case)
    )

  case class TokenResponse(
      name: Option[String],
      symbol: Option[String],
      address: String
  ):
    def toERC20Token: Option[ERC20Token] =
      symbol.flatMap(ERC20Token(_, address))

  case class TokenBalancesResponse(token: TokenResponse, value: Balance)

  object TokenBalancesResponse:

    given codec: JsonValueCodec[Set[TokenBalancesResponse]] =
      JsonCodecMaker.make

  val layer: ZLayer[Client, Config.Error, BlockscoutClient] =
    ZLayer {
      for
        config <- ZIO.config(BlockscoutConfig.config)
        client <- ZIO.service[Client]
      yield BlockscoutClient(config, client)
    }
