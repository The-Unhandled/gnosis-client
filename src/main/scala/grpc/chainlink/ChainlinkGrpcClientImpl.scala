package xyz.forsaken.gnosisclient
package grpc.chainlink

import zio.*
import io.grpc.ManagedChannelBuilder
import grpc.GrpcConfig

final class ChainlinkGrpcClientImpl(stub: ChainlinkServiceGrpc.ChainlinkServiceStub) extends ChainlinkGrpcClient:
  override def getPriceFeed(request: PriceFeedRequest): Task[PriceFeedResponse] =
    ZIO.fromFuture(_ => stub.getPriceFeed(request))

object ChainlinkGrpcClientImpl:
  val layer: ZLayer[Any, Throwable, ChainlinkGrpcClient] = ZLayer.scoped {
    for {
      config <- ZIO.config(GrpcConfig.config)
      channel <- ZIO.acquireRelease {
        ZIO.attempt {
          ManagedChannelBuilder
            .forAddress(config.host, config.port)
            .usePlaintext()
            .build()
        }
      }(ch => ZIO.succeed(ch.shutdown()).ignore)
      stub <- ZIO.attempt(ChainlinkServiceGrpc.stub(channel))
    } yield ChainlinkGrpcClientImpl(stub)
  }
