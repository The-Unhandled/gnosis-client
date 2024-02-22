package xyz.forsaken.gnosisclient
package gnosis

/** @author
  *   Petros Siatos
  */
object Tokens:

  trait ERC20Token(
      val name: String,
      val address: String
  )

  case object WXDAI
      extends ERC20Token("wXdai", "0xe91D153E0b41518A2Ce8Dd3D7944Fa863463a97d")

  case object SDAI
      extends ERC20Token("sDai", "0xaf204776c7245bF4147c2612BF6e5972Ee483701")
