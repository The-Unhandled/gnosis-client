package xyz.forsaken.gnosisclient
package discord

import zio.Config
import zio.config.magnolia.deriveConfig

import java.net.URI

final case class DiscordConfig(apiUrl: URI, gatewayUrl: URI, botToken: String)

object DiscordConfig:
  val config: Config[DiscordConfig] =
    deriveConfig[DiscordConfig].nested("Discord")
