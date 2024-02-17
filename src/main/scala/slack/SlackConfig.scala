package xyz.forsaken.gnosisclient
package slack

import zio.Config
import zio.config.magnolia.deriveConfig
import java.net.URI

case class SlackConfig(webhook: URI)

object SlackConfig:
  val config: Config[SlackConfig] =
    deriveConfig[SlackConfig].nested("Slack")
