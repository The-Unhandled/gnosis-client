package xyz.forsaken.gnosisclient
package cowswap

import com.github.plokhotnyuk.jsoniter_scala.core.*

opaque type CowSwapQuoteAmount = Double

object CowSwapQuoteAmount:

  final val ZERO = CowSwapQuoteAmount(0)

  def apply(value: Double): CowSwapQuoteAmount = value

  given quoteAmountCodec: JsonValueCodec[CowSwapQuoteAmount] = new JsonValueCodec[CowSwapQuoteAmount]:
    override def decodeValue(in: JsonReader, default: CowSwapQuoteAmount): CowSwapQuoteAmount =
      (BigDecimal(in.readString("0")) / 1e18).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble

    override def encodeValue(x: CowSwapQuoteAmount, out: JsonWriter): Unit = out.writeVal(x)

    override def nullValue: CowSwapQuoteAmount = ZERO

  given Conversion[CowSwapQuoteAmount, Double] = identity
