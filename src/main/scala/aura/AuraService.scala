package xyz.forsaken.gnosisclient
package aura

import grpc.aura.{AuraGrpcClient, AuraRequest}

import zio.*

trait AuraService:
  def getBalance(address: String): Task[(Double, Double)]

final class AuraServiceImpl(client: AuraGrpcClient) extends AuraService:
  override def getBalance(address: String): Task[(Double, Double)] =
    for {
      response <- client.getAuraBalance(AuraRequest(address))
    } yield (response.balance, response.earned)

object AuraServiceImpl:
  val layer: ZLayer[AuraGrpcClient, Throwable, AuraService] =
    ZLayer.fromFunction(AuraServiceImpl.apply)
