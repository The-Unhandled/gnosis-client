package xyz.forsaken.gnosisclient
package blockscout

import zio.Config
import zio.config.magnolia.deriveConfig

import java.net.URI

final case class BlockscoutConfig(apiKey: String, url: URI)

object BlockscoutConfig:
  val config: Config[BlockscoutConfig] =
    deriveConfig[BlockscoutConfig].nested("Blockscout")
