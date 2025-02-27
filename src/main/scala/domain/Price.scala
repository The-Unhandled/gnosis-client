package xyz.forsaken.gnosisclient
package domain

import com.github.plokhotnyuk.jsoniter_scala.core.*

opaque type Price = Double

object Price:

  final val ZERO = Price(0)

  def apply(value: Double): Price = value

  given priceCodec: JsonValueCodec[Price] = new JsonValueCodec[Price]:
    override def decodeValue(in: JsonReader, default: Price): Price = in.readString("0").toDouble

    override def encodeValue(x: Price, out: JsonWriter): Unit = out.writeVal(x)

    override def nullValue: Price = ZERO

  given Conversion[Price, Double] = identity