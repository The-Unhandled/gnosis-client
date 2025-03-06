package xyz.forsaken.gnosisclient
package chainlink

import grpc.chainlink.*
import zio.*

trait ChainlinkService:
  def getPriceFeed(chain: String, pair: String): Task[Double]

final class ChainlinkServiceImpl(client: ChainlinkGrpcClient) extends ChainlinkService:
  override def getPriceFeed(chain: String, pair: String): Task[Double] =
    for
      response <- client.getPriceFeed(PriceFeedRequest(chain, pair))
    yield response.price

object ChainlinkServiceImpl:
  val layer: ZLayer[ChainlinkGrpcClient, Throwable, ChainlinkService] =
    ZLayer.fromFunction(ChainlinkServiceImpl.apply)
