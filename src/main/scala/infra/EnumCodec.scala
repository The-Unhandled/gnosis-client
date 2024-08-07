package xyz.forsaken.gnosisclient
package infra

/**
 * @author Petros Siatos
 */
import com.github.plokhotnyuk.jsoniter_scala.core.*

object EnumCodec {
  def createEnumCodec[E <: Enum[E]](enumClass: Class[E]): JsonValueCodec[E] = new JsonValueCodec[E] {
    override def decodeValue(in: JsonReader, default: E): E =
      Enum.valueOf(enumClass, in.readString("").toUpperCase)

    override def encodeValue(e: E, out: JsonWriter): Unit =
      out.writeVal(e.toString)

    override def nullValue: E =
      enumClass.getEnumConstants.head
  }
}