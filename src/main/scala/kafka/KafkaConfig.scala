package xyz.forsaken.gnosisclient
package kafka

import zio.Config
import zio.config.magnolia.deriveConfig

final case class KafkaConfig(
                              bootstrapServers: String,
                              topic: String,
                              groupId: String
                            )

object KafkaConfig:
  val config: Config[KafkaConfig] =
    deriveConfig[KafkaConfig].nested("Kafka")
