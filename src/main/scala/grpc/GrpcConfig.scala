package xyz.forsaken.gnosisclient
package grpc

import zio.Config
import zio.config.magnolia.deriveConfig

final case class GrpcConfig(host: String, port: Int)

object GrpcConfig:
  val config: Config[GrpcConfig] =
    deriveConfig[GrpcConfig].nested("Grpc")
