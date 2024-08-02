package xyz.forsaken.gnosisclient
package gnosisscan

import domain.Tokens.ERC20Token
import domain.gnosis.xDai

import zio.Task

/**
 * @author Petros Siatos
 */
trait GethProxyClient:
  def getBalance(token: ERC20Token, address: String): Task[xDai]
