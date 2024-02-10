package xyz.forsaken.gnosisclient

import zio.Config
import zio.config.magnolia.deriveConfig

case class GnosisConfig(port: Int)

object GnosisConfig:
  val config: Config[GnosisConfig] =
    deriveConfig[GnosisConfig].nested("GnosisConfig")
