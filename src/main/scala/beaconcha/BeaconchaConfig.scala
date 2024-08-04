package xyz.forsaken.gnosisclient
package beaconcha

import zio.Config
import zio.config.magnolia.deriveConfig

import java.net.URI

final case class BeaconchaConfig(apiKey: String, url: URI)

object BeaconchaConfig:
  val config: Config[BeaconchaConfig] =
    deriveConfig[BeaconchaConfig].nested("Beaconcha")
