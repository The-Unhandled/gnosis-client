package xyz.forsaken.gnosisclient
package domain

import domain.TokenBalance

import zio.*

/** @author
  *   Petros Siatos
  */
trait TokensClient:
  def getTokenBalances(address: String): Task[Set[TokenBalance]]
