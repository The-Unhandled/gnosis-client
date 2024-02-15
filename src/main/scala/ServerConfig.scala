package xyz.forsaken.gnosisclient

import zio.Config
import zio.config.magnolia.deriveConfig

case class ServerConfig(port: Int)

object ServerConfig:
  val config: Config[ServerConfig] =
    deriveConfig[ServerConfig].nested("Server")
