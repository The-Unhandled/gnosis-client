package xyz.forsaken.gnosisclient
package cowswap

import zio.Config
import zio.config.magnolia.deriveConfig
import java.net.URI

final case class CowSwapConfig(apiKey: String, url: URI)

object CowSwapConfig:
  val config: Config[CowSwapConfig] =
    deriveConfig[CowSwapConfig].nested("CowSwap")
