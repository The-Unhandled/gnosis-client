package xyz.forsaken.gnosisclient
package server

import com.github.plokhotnyuk.jsoniter_scala.core.{
  JsonValueCodec,
  readFromString,
  writeToString
}
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.{Codec, DecodeResult, Schema}

/** @author
  *   Petros Siatos
  */
trait JsoniterToTapirCodec:

  /** Implicit conversion from JsonValueCodec to Tapir Codec
    */
  given jsoniterToTapirCodec[A](using
      codec: JsonValueCodec[A],
      schema: Schema[A]
  ): JsonCodec[A] =
    Codec.json[A](json =>
      try {
        DecodeResult.Value(readFromString[A](json))
      } catch {
        case e: Exception => DecodeResult.Error(json, e)
      }
    )(a => writeToString(a))

object JsoniterToTapirCodec extends JsoniterToTapirCodec
