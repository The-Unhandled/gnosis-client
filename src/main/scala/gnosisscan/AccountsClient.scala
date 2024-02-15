package xyz.forsaken.gnosisclient
package gnosisscan

import gnosis.xDai

import zio.*

/** Client for Gnosisscan Accounts
  * @see
  *   [[https://docs.gnosisscan.io/api-endpoints/accounts GnosisScan Docs]]
  */
trait AccountsClient:
  def getxDaiBalance(address: String): Task[xDai]
