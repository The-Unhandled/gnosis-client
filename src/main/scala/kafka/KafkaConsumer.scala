package xyz.forsaken.gnosisclient
package kafka

import slack.SlackClient

import zio.*
import zio.kafka.consumer.*
import zio.kafka.serde.*

final class KafkaConsumer(consumer: Consumer, config: KafkaConfig, slackClient: SlackClient):
  def runConsumer: RIO[Any, Unit] =
    ZIO.scoped {
      for
        _ <- ZIO.logInfo(s"📡 Connected to Kafka at ${config.bootstrapServers}")
        _ <- consumer
          .plainStream(Subscription.topics(config.topic), Serde.string, Serde.string)
          .tap { record =>
            val message = s"🔹 Received Ethereum Log: ${record.value}"
            slackClient.notify(message)
          }
          .map(_.offset)
          .aggregateAsync(Consumer.offsetBatches)
          .mapZIO(_.commit)
          .runDrain
      yield ()
    }

object KafkaConsumer:
  val layer: ZLayer[SlackClient, Throwable, KafkaConsumer] =
    ZLayer.scoped {
      for
        config <- ZIO.config(KafkaConfig.config)
        slackClient <- ZIO.service[SlackClient]
        consumer <- Consumer.make(
          ConsumerSettings(List(config.bootstrapServers))
            .withGroupId(config.groupId)
        )
      yield KafkaConsumer(consumer, config, slackClient)
    }