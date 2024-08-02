package xyz.forsaken.gnosisclient
package domain

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

/**
 * @author Petros Siatos
 */
opaque type Balance = Double

object Balance:
  
  final val ZERO = Balance(0)
  
  private final val WEI_DIVISOR = BigDecimal(10).pow(18)

  def apply(value: Double): Balance = value

  private def fromWei(wei: String): Balance = fromWei(BigDecimal(wei))

  private def fromWei(wei: BigDecimal): Balance =
    (wei / Balance.WEI_DIVISOR)
      .setScale(2, BigDecimal.RoundingMode.HALF_UP)
      .toDouble

  given balanceCodec: JsonValueCodec[Balance] = new JsonValueCodec[Balance]:
    override def decodeValue(in: JsonReader, default: Balance): Balance = fromWei(in.readString("0"))

    override def encodeValue(x: Balance, out: JsonWriter): Unit = out.writeVal(x)

    override def nullValue: Balance = ZERO
