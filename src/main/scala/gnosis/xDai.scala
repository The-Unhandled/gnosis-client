package xyz.forsaken.gnosisclient
package gnosis

/** @author
  *   Petros Siatos
  *
  * xDAI
  */
case class xDai(value: Double)

object xDai:

  private val WEI_DIVISOR = BigDecimal(10).pow(18)

  def fromWei(wei: BigDecimal): xDai =
    xDai(
      (wei / xDai.WEI_DIVISOR)
        .setScale(2, BigDecimal.RoundingMode.HALF_UP)
        .toDouble
    )
