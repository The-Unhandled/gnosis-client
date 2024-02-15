package xyz.forsaken.gnosisclient
package gnosisscan

import zio.Config
import zio.config.magnolia.deriveConfig
import java.net.URI

final case class GnosisScanConfig(apiKey: String, url: URI)

object GnosisScanConfig:
  val config: Config[GnosisScanConfig] =
    deriveConfig[GnosisScanConfig].nested("GnosisScan")
