package xyz.forsaken.gnosisclient
package beaconcha

import domain.*
import domain.Tokens.ERC20Token
import domain.gnosis.xDai
import infra.CommonHttpClient
import validators.*

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.*
import zio.http.*

import javax.naming.ConfigurationException

final class BeaconchaClient(
    val config: BeaconchaConfig,
    httpClient: Client,
    validators: Set[Int]
) extends CommonHttpClient
    with ValidatorsClient:

  import BeaconchaClient.*

  final val uri = config.url
  final val apiKey = config.apiKey

  override def getValidators: Task[Set[Validator]] =
    (for
      url <- getUrl
      createurl = url
        ./("validator")
        ./(validators.mkString(","))
      response <- request[BeaconchaResponse](createurl)
      validators = response.data.map(_.toValidator)
    yield validators).provide(ZLayer.succeed(httpClient), Scope.default)

object BeaconchaClient:

  case class ValidatorResponse(
      validatorindex: Long,
      balance: Long,
      status: String,
      pubkey: String
  ):
    def toValidator: Validator = Validator(
      index = validatorindex,
      balance = balance,
      status = status,
      address = pubkey
    )

  case class BeaconchaResponse(status: String, data: Set[ValidatorResponse])

  object BeaconchaResponse:
    given codec: JsonValueCodec[BeaconchaResponse] = JsonCodecMaker.make

  val layer: ZLayer[Client, Config.Error, BeaconchaClient] =
    ZLayer {
      for
        config <- ZIO.config(BeaconchaConfig.config)
        validators <- ZIO.config(ValidatorsConfig.config)
        client <- ZIO.service[Client]
      yield BeaconchaClient(config, client, validators.indices)
    }
