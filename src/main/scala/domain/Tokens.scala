package xyz.forsaken.gnosisclient
package domain

/** @author
  *   Petros Siatos
  */
object Tokens:

  enum ERC20Token(val symbol: String, val address: String):
    case WETH
        extends ERC20Token("WETH", "0x6A023CCd1ff6F2045C3309768eAd9E68F978f6e1")
    case WSTETH
        extends ERC20Token(
          "WSTETH",
          "0x6C76971f98945AE98dD7d4DFcA8711ebea946eA6"
        )
    case WXDAI
        extends ERC20Token(
          "WXDAI",
          "0xe91D153E0b41518A2Ce8Dd3D7944Fa863463a97d"
        )
    case SDAI
        extends ERC20Token("SDAI", "0xaf204776c7245bF4147c2612BF6e5972Ee483701")

  object ERC20Token:
    def apply(symbol: String, address: String): Option[ERC20Token] =
      values.find(t => t.symbol == symbol && t.address == address)
