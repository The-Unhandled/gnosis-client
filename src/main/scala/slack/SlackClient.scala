package xyz.forsaken.gnosisclient
package slack

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.*
import zio.http.*
import zio.http.Header.ContentType

import javax.naming.ConfigurationException

trait SlackClient:
  def notify(message: String): Task[Unit]

/** @author
  *   Petros Siatos
  */
class SlackClientLayer(config: SlackConfig, httpClient: Client)
    extends SlackClient:

  final val SLACK_CHANNEL = "gnosis-client"

  case class SlackMessage(text: String)

  object SlackMessage:
    given codec: JsonValueCodec[SlackMessage] = JsonCodecMaker.make

  def notify(message: String): Task[Unit] =

    // FIXME:
    val url: URL = URL
      .fromURI(config.webhook)
      .getOrElse(throw new ConfigurationException("Invalid URL"))

    (for
      response <- httpClient
        .url(url)
        .addHeader(Header.ContentType(MediaType.application.json))
        .post("")(Body.fromString(writeToString(SlackMessage(message))))

      _ <- ZIO.logInfo(s"response: $response")
      _ <- ZIO.logInfo(s"Slack notification sent to $SLACK_CHANNEL: $message")
    yield ()).provideSomeLayer(Scope.default)

object SlackClientLayer:
  val layer: ZLayer[Client, Config.Error, SlackClient] =
    ZLayer {
      for
        config <- ZIO.config(SlackConfig.config)
        client <- ZIO.service[Client]
      yield SlackClientLayer(config, client)
    }
