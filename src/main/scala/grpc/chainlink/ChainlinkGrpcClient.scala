package xyz.forsaken.gnosisclient
package grpc.chainlink

import zio.*

trait ChainlinkGrpcClient:
  def getPriceFeed(request: PriceFeedRequest): Task[PriceFeedResponse]
