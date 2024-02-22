package xyz.forsaken.gnosisclient
package server

import slack.SlackClient

import xyz.forsaken.gnosisclient.gnosis.Tokens.ERC20Token
import xyz.forsaken.gnosisclient.gnosisscan.{AccountsClient, GethProxyClient}
import zio.ZLayer
import zio.http.*
import xyz.forsaken.gnosisclient.gnosis.Tokens.*

trait BalanceEndpoint extends Endpoint:
  override def routes: Routes[Any, Throwable] = Routes(xdaiBalanceRoute)

  def xdaiBalanceRoute: Route[Any, Throwable]

final class BalanceEndpointImpl(
    accountsClient: AccountsClient,
    gethProxyClient: GethProxyClient,
    slackClient: SlackClient
) extends BalanceEndpoint:

  def xdaiBalanceRoute: Route[Any, Throwable] =
    Method.GET / "balance" / string("address") -> handler {
      (address: String, req: Request) =>
        for
          xDaiBalance <- accountsClient.getxDaiBalance(address)
          sDaiBalance <- gethProxyClient.getBalance(SDAI, address)
          wxDaiBalance <- gethProxyClient.getBalance(WXDAI, address)
          message = s"Your balance is: $xDaiBalance xDai, $sDaiBalance sDai, $wxDaiBalance wxDai"
          _ <- slackClient.notify(message)
        yield Response.text(message)
    }

object BalanceEndpointImpl:
  val layer = ZLayer.fromFunction(BalanceEndpointImpl(_, _, _))
