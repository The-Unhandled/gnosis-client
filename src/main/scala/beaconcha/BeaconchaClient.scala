package xyz.forsaken.gnosisclient
package beaconcha

import domain.*
import domain.Tokens.ERC20Token
import domain.gnosis.xDai
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
) extends ValidatorsClient:

  import BeaconchaClient.*

  override def getValidators: Task[Set[Validator]] =
    // FIXME:
    val url: URL = URL
      .fromURI(config.url)
      .getOrElse(throw new ConfigurationException("Invalid URL"))
      ./("validator")
      ./(validators.mkString(","))
      .addQueryParams(QueryParams("apikey" -> config.apiKey))

    (for
      response <- httpClient.url(url).get("")
      responseBody <- response.body.asString
      beaconchaResponse <- ZIO
        .attempt(
          readFromString[BeaconchaResponse](responseBody)
        )
      validators = beaconchaResponse.data.map(_.toValidator)
    yield validators).provideSomeLayer(Scope.default)

object BeaconchaClient:

  case class ValidatorResponse(validatorindex: Long, balance: Long, status: String, pubkey: String):
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
