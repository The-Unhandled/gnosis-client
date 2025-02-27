package xyz.forsaken.gnosisclient
package server

import domain.ValidatorsClient
import slack.SlackClient
import validators.Validator

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.*
import sttp.tapir.{AnyEndpoint, PublicEndpoint, Schema}
import zio.http.*
import zio.{Task, ZIO, ZLayer}

trait ValidatorEndpoint extends TapirEndpoint:
  override def routes: Routes[Any, Response] = getValidatorsRoute

  def getValidatorsRoute: Routes[Any, Response]

final class ValidatorEndpointImpl(
    validatorsClient: ValidatorsClient,
    slackClient: SlackClient
) extends ValidatorEndpoint
    with JsoniterToTapirCodec:

  given codec: JsonValueCodec[Set[Validator]] = JsonCodecMaker.make

  private def getValidatorsLogic: Task[Set[Validator]] =
    for
      validators <- validatorsClient.getValidators
      message = validators.mkString("\n")
      _ <- slackClient.notify("Validators: \n" + message)
    yield validators

  private def getValidatorsEndpoint
      : PublicEndpoint[Unit, String, Set[Validator], Any] =
    baseEndpoint
      .in("validators")
      .out(customCodecJsonBody[Set[Validator]])

  override def endpoints: List[AnyEndpoint] = getValidatorsEndpoint :: Nil

  def getValidatorsRoute: Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(
      wrapLogic(getValidatorsEndpoint, _ => getValidatorsLogic)
    )

object ValidatorEndpointImpl:
  val layer = ZLayer.fromFunction(ValidatorEndpointImpl(_, _))
