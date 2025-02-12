package xyz.forsaken.gnosisclient
package grpc.aura

import io.grpc.ManagedChannelBuilder
import grpc.GrpcConfig

import zio.*

final class AuraGrpcClientImpl(stub: AuraServiceGrpc.AuraServiceStub) extends AuraGrpcClient:
  override def getAuraBalance(request: AuraRequest): Task[AuraResponse] =
    ZIO.fromFuture(_ => stub.getAuraBalance(request))

object AuraGrpcClientImpl:
  val layer: ZLayer[Any, Throwable, AuraGrpcClient] = ZLayer.scoped {
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
      stub <- ZIO.attempt(AuraServiceGrpc.stub(channel))
    } yield AuraGrpcClientImpl(stub)
  }
