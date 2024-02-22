package xyz.forsaken.gnosisclient
package gnosisscan

import gnosis.xDai

import xyz.forsaken.gnosisclient.gnosis.Tokens.ERC20Token
import zio.Task

/**
 * @author Petros Siatos
 */
trait GethProxyClient:
  def getBalance(token: ERC20Token, address: String): Task[xDai]
