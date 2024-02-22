package xyz.forsaken.gnosisclient
package gnosisscan

import zio.*

/** @author
  *   Petros Siatos
  */
trait ContractsClient:
  def getAbi(address: String): Task[String]
