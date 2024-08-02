package xyz.forsaken.gnosisclient
package server

import domain.*
import gnosisscan.GethProxyClient
import slack.SlackClient

import zio.ZLayer
import zio.http.*

trait BalanceEndpoint extends Endpoint:
  override def routes: Routes[Any, Throwable] = Routes(xdaiBalanceRoute)

  def xdaiBalanceRoute: Route[Any, Throwable]

final class BalanceEndpointImpl(
    accountsClient: AccountsClient,
    tokensClient: TokensClient,
    gethProxyClient: GethProxyClient,
    slackClient: SlackClient
) extends BalanceEndpoint:

  def xdaiBalanceRoute: Route[Any, Throwable] =
    Method.GET / "balance" / string("address") -> handler {
      (address: String, req: Request) =>
        for
          xDaiBalance <- accountsClient.getxDaiBalance(address)
          tokensBalance <- tokensClient.getTokenBalances(address)
          //sDaiBalance <- gethProxyClient.getBalance(SDAI, address)
          //wxDaiBalance <- gethProxyClient.getBalance(WXDAI, address)
          //message = s"Your balance is: $xDaiBalance xDai, $sDaiBalance sDai, $wxDaiBalance wxDai"
          tokensBalanceMessage = tokensBalance.map(tb => s"${tb.token.symbol}: ${tb.balance}").mkString(", ")
          message = s"Your balance is: $xDaiBalance xDai\n$tokensBalanceMessage"
          _ <- slackClient.notify(message)
        yield Response.text(message)
    }

object BalanceEndpointImpl:
  val layer = ZLayer.fromFunction(BalanceEndpointImpl(_, _, _, _))
