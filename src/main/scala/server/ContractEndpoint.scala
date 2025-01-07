package xyz.forsaken.gnosisclient
package server

import gnosisscan.ContractsClient
import slack.SlackClient

import sttp.tapir.{AnyEndpoint, PublicEndpoint}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.*
import zio.*
import zio.http.{Response, Route, Routes}

trait ContractEndpoint extends TapirEndpoint:
  override def routes: Routes[Any, Response] = getAbiRoute

  def getAbiRoute: Routes[Any, Response]

final class ContractEndpointImpl(
    contractsClient: ContractsClient,
    slackClient: SlackClient
) extends ContractEndpoint:

  private def getAbiLogic(address: String): Task[String] =
    for
      contract <- contractsClient.getAbi(address)
      _ <- slackClient.notify(s"Your contract is: $contract")
    yield contract

  private def getAbiEndpoint: PublicEndpoint[String, String, String, Any] =
    baseEndpoint
      .in("contract")
      .in(path[String]("address"))
      .out(stringBody)

  override def endpoints: List[AnyEndpoint] = getAbiEndpoint :: Nil

  def getAbiRoute: Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(wrapLogic(getAbiEndpoint, getAbiLogic))

object ContractEndpointImpl:
  val layer = ZLayer.fromFunction(ContractEndpointImpl(_, _))
