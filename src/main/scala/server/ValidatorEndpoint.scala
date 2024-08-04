package xyz.forsaken.gnosisclient
package server

import beaconcha.BeaconchaClient.BeaconchaResponse
import domain.ValidatorsClient
import slack.SlackClient
import validators.Validator

import com.github.plokhotnyuk.jsoniter_scala.core.{JsonValueCodec, writeToString}
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import zio.ZLayer
import zio.http.*

trait ValidatorEndpoint extends Endpoint:
  override def routes: Routes[Any, Throwable] = Routes(getValidators)

  def getValidators: Route[Any, Throwable]

final class ValidatorEndpointImpl(
    validatorsClient: ValidatorsClient,
    slackClient: SlackClient
) extends ValidatorEndpoint:

  def getValidators: Route[Any, Throwable] =

    given codec: JsonValueCodec[Set[Validator]] = JsonCodecMaker.make
    
    Method.GET / "validators" -> handler {
      for
        validators <- validatorsClient.getValidators
        message = validators.mkString("\n")
        _ <- slackClient.notify("Validators: \n" + message)
      yield Response.json(writeToString(validators))
    }

object ValidatorEndpointImpl:
  val layer = ZLayer.fromFunction(ValidatorEndpointImpl(_, _))
