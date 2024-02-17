package xyz.forsaken.gnosisclient
package gnosisscan
import xyz.forsaken.gnosisclient.slack.SlackClient
import zio.ZLayer
import zio.http.*

trait BalanceEndpoint extends Endpoint:
  override def routes: Routes[Any, Throwable] = Routes(xdaiBalanceRoute)

  def xdaiBalanceRoute: Route[Any, Throwable]

final class BalanceEndpointLayer(accountsClient: AccountsClient, slackClient: SlackClient)
    extends BalanceEndpoint:

  def xdaiBalanceRoute: Route[Any, Throwable] =
    Method.GET / "balance" / string("address") -> handler {
      (address: String, req: Request) =>
        for 
          balance <- accountsClient.getxDaiBalance(address)
          _ <- slackClient.notify(s"Your xDai balance is: $balance")
        yield Response.text(s"xDai Balance: $balance")
    }

object BalanceEndpointLayer:
  val layer = ZLayer.fromFunction(BalanceEndpointLayer(_, _))
