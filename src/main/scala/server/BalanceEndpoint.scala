package xyz.forsaken.gnosisclient
package server

import domain.*
import gnosisscan.GethProxyClient
import slack.SlackClient

import sttp.tapir.PublicEndpoint
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.*
import zio.*
import zio.http.{Response, Route, Routes}

trait BalanceEndpoint extends TapirEndpoint:
  override def routes: Routes[Any, Response] = xdaiBalanceRoute

  def xdaiBalanceRoute: Routes[Any, Response]

final class BalanceEndpointImpl(
    accountsClient: AccountsClient,
    tokensClient: TokensClient,
    gethProxyClient: GethProxyClient,
    slackClient: SlackClient
) extends BalanceEndpoint:

  private def xdaiBalanceLogic(address: String): ZIO[Any, Throwable, String] =
    for
      xDaiBalance <- accountsClient.getxDaiBalance(address)
      tokensBalance <- tokensClient.getTokenBalances(address)
      tokensBalanceMessage = tokensBalance
        .map(tb => s"${tb.token.symbol}: ${tb.balance}")
        .mkString(", ")
      message = s"Your balance is: $xDaiBalance xDai\n$tokensBalanceMessage"
      _ <- slackClient.notify(message)
    yield message

  private def xdaiBalanceEndpoint: PublicEndpoint[String, String, String, Any] =
    baseEndpoint
      .in("balance")
      .in(path[String]("address"))
      .out(stringBody)

  def xdaiBalanceRoute: Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(
      wrapLogic(xdaiBalanceEndpoint, xdaiBalanceLogic)
    )

object BalanceEndpointImpl:
  val layer = ZLayer.fromFunction(BalanceEndpointImpl(_, _, _, _))
