package xyz.forsaken.gnosisclient

package grpc.aura

import zio.*

trait AuraGrpcClient:
  def getAuraBalance(request: AuraRequest): Task[AuraResponse]
