package xyz.forsaken.gnosisclient
package domain

import domain.Tokens.ERC20Token

/** @author
  *   Petros Siatos
  */
case class TokenBalance(token: ERC20Token, balance: Balance)
