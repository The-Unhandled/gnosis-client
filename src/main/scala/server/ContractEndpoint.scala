package xyz.forsaken.gnosisclient
package server

import gnosisscan.ContractsClient
import slack.SlackClient

import zio.ZLayer
import zio.http.*

trait ContractsEndpoint extends Endpoint:
  override def routes: Routes[Any, Throwable] = Routes(getAbiRoute)

  def getAbiRoute: Route[Any, Throwable]

final class ContractEndpointImpl(
    contractsClient: ContractsClient,
    slackClient: SlackClient
) extends ContractsEndpoint:

  def getAbiRoute: Route[Any, Throwable] =
    Method.GET / "contract" / string("address") -> handler {
      (address: String, req: Request) =>
        for
          contract <- contractsClient.getAbi(address)
          _ <- slackClient.notify(s"Your contract is: $contract")
        yield Response.text(contract)
    }

object ContractEndpointImpl:
  val layer = ZLayer.fromFunction(ContractEndpointImpl(_, _))
