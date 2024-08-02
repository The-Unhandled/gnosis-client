package xyz.forsaken.gnosisclient
package domain.gnosis

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

/** @author
  *   Petros Siatos
  *
  * xDAI
  */
opaque type wxDai = xDai
opaque type xDai = Double

object xDai:
  
  final val ZERO = xDai(0)

  def apply(d: Double): xDai = d

  private val WEI_DIVISOR = BigDecimal(10).pow(18)

  def fromHexadecimal(hex: String): xDai =
    fromWei(BigDecimal(BigInt(hex.substring(2), 16)))

  def fromWei(wei: String): xDai = fromWei(BigDecimal(wei))

  def fromWei(wei: BigDecimal): xDai =
    (wei / xDai.WEI_DIVISOR)
      .setScale(2, BigDecimal.RoundingMode.HALF_UP)
      .toDouble

  given xdaiCodec: JsonValueCodec[xDai] = new JsonValueCodec[xDai]:
    override def decodeValue(in: JsonReader, default: xDai): xDai = fromWei(in.readString("0"))
    
    override def encodeValue(x: xDai, out: JsonWriter): Unit = out.writeVal(x)

    override def nullValue: xDai = ZERO
