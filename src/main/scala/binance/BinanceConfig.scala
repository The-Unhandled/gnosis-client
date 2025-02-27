package xyz.forsaken.gnosisclient
package binance

import zio.Config
import zio.config.magnolia.deriveConfig

import java.net.URI

final case class BinanceConfig(apiKey: String, url: URI)

object BinanceConfig:
  val config: Config[BinanceConfig] =
    deriveConfig[BinanceConfig].nested("Binance")
