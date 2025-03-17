package xyz.forsaken.gnosisclient
package discord

import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import sttp.capabilities.zio.ZioStreams
import sttp.client4.*
import sttp.client4.httpclient.zio.*
import sttp.client4.ws.stream.*
import sttp.model.Uri
import sttp.ws.WebSocketFrame
import zio.stream.ZStream
import zio.{stream, *}
import discord.CirceCustomDecoders.*

import java.time.Instant
import java.time.format.DateTimeFormatter

final class DiscordClient(
    val config: DiscordConfig,
    sttpBackend: SttpClient
):
  import DiscordClient.*

  def connectWebSocket() = {

    val pipe =
      (stream: ZStream[Any, Throwable, WebSocketFrame.Data[?]]) =>
        stream.mapZIO {
          case WebSocketFrame.Text(payload, _, _) =>
            for {
              eventResult <- processWebSocketMessage(payload)
              response <-
                if (eventResult.exists(_.op == 10)) {
                  ZIO.succeed(identifyMessage)
                } else {
                  ZIO.succeed(WebSocketFrame.Pong(Array.emptyByteArray))
                }
            } yield response
          case other =>
            ZIO
              .logDebug(s"Received frame: $other")
              .as(WebSocketFrame.Pong(Array.emptyByteArray))
        }

    val wsRequest = basicRequest
      .get(
        Uri(config.gatewayUrl)
          .addParam("v", "10")
          .addParam("encoding", "json")
      )
      .response(asWebSocketStream(ZioStreams)(pipe)) // Use the correct pipe

    sttpBackend.send(wsRequest).forkScoped
  }

  private def identifyMessage: WebSocketFrame.Text = {
    // Define payload classes
    case class Properties(
        `$os`: String,
        `$browser`: String,
        `$device`: String
    )

    case class IdentifyData(
        token: String,
        intents: Int,
        properties: Properties
    )

    case class IdentifyPayload(
        op: Int,
        d: IdentifyData
    )

    // Create the identify payload
    val payload = IdentifyPayload(
      op = 2,
      d = IdentifyData(
        token = config.botToken,
        intents = 513 | 32768,
        properties = Properties(
          `$os` = "linux",
          `$browser` = "sttp",
          `$device` = "sttp"
        )
      )
    )

    // Create the payload as a string
    val jsonPayload = payload.asJson.noSpaces
    WebSocketFrame.text(jsonPayload)
  }

  private def processWebSocketMessage(payload: String): Task[Option[GatewayEvent]] = {
    ZIO.fromEither(decode[GatewayEvent](payload))
      .flatMap { gatewayEvent =>
        gatewayEvent.t match {
          case Some("MESSAGE_CREATE") =>
            gatewayEvent.d match {
              case Some(jsonObj) =>
                ZIO.fromEither(decode[Message](jsonObj.asJson.noSpaces))
                  .flatMap { message =>
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val formattedTime = formatter.format(message.timestamp.atZone(java.time.ZoneId.systemDefault()))
                    ZIO.logInfo(s"Received message: $formattedTime - ${message.content} from ${message.author.global_name}")
                  }
                  .catchAll(err => ZIO.logError(s"Failed to parse message: ${err.getMessage}"))
                  .as(Some(gatewayEvent))
              case None => ZIO.some(gatewayEvent)
            }
          case Some(eventType) =>
            ZIO.logInfo(s"Received event: $eventType").as(Some(gatewayEvent))
          case None => ZIO.some(gatewayEvent)
        }
      }
      .catchAll(err => {
        ZIO.logError(s"Error processing websocket message: ${err.getMessage}").as(None)
      })
  }
  
object DiscordClient:
  // JSON codecs and data models
  private final class MessageRequest(content: String)

  private final case class Author(id: String, username: String, global_name: String)

  private final case class Message(
      id: String,
      channel_id: String,
      author: Author,
      content: String,
      timestamp: Instant
  )

  private final case class GatewayEvent(
      op: Int,
      d: Option[JsonObject],
      s: Option[Int],
      t: Option[String]
  )

  val layer: ZLayer[SttpClient, Throwable, DiscordClient] =
    ZLayer {
      for {
        backend <- ZIO.service[SttpClient]
        config <- ZIO.config(DiscordConfig.config)
      } yield DiscordClient(config, backend)
    }
