package xyz.forsaken.gnosisclient
package gnosis

/** @author
  *   Petros Siatos
  *
  * xDAI
  */
opaque type xDai = Double

object xDai:

  private val WEI_DIVISOR = BigDecimal(10).pow(18)

  def fromHexadecimal(hex: String): xDai =
    fromWei(BigDecimal(BigInt(hex.substring(2), 16)))

  def fromWei(wei: BigDecimal): xDai =
    (wei / xDai.WEI_DIVISOR)
      .setScale(2, BigDecimal.RoundingMode.HALF_UP)
      .toDouble
