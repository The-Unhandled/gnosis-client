package xyz.forsaken.gnosisclient
package gnosisscan
import zio.ZLayer
import zio.http.*

trait BalanceEndpoint extends Endpoint:
  override def routes: Routes[Any, Throwable] = Routes(xdaiBalanceRoute)

  def xdaiBalanceRoute: Route[Any, Throwable]

final class BalanceEndpointLayer(accountsClient: AccountsClient)
    extends BalanceEndpoint:

  def xdaiBalanceRoute: Route[Any, Throwable] =
    Method.GET / "balance" / string("address") -> handler {
      (address: String, req: Request) =>
        for balance <- accountsClient.getxDaiBalance(address)
        yield Response.text(s"xDai Balance: $balance")
    }

object BalanceEndpointLayer:
  val layer = ZLayer.fromFunction(BalanceEndpointLayer(_))
