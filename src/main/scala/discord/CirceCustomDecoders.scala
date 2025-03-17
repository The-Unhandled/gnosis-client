package xyz.forsaken.gnosisclient
package discord

import java.time.Instant
import java.time.format.DateTimeFormatter
import io.circe.{Decoder, Encoder}

object CirceCustomDecoders:
  implicit val instantDecoder: Decoder[Instant] = Decoder.decodeString.emap { str =>
    try {
      // Parse ISO-8601 format with potential microseconds
      Right(Instant.parse(str))
    } catch {
      case e: Exception => Left(s"Failed to parse date: ${e.getMessage}")
    }
  }

  implicit val instantEncoder: Encoder[Instant] = Encoder.encodeString.contramap(_.toString)

