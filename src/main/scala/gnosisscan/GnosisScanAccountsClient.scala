package xyz.forsaken.gnosisclient
package gnosisscan

import domain.AccountsClient
import domain.gnosis.xDai
import gnosisscan.GnosisScanClient.Module.Account

import zio.*
import zio.http.*

final class GnosisScanAccountsClient(
    val config: GnosisScanConfig,
    httpClient: Client
) extends GnosisScanClient
    with AccountsClient:

  final protected val module = Account

  override def getxDaiBalance(address: String): Task[xDai] =
    (for
      url <- getUrl("balance")
      createurl = url
        .addQueryParams(QueryParams("address" -> address))
      xDai <- request[ResponseBody](createurl)
        .map(x => xDai.fromWei(BigDecimal(x.result)))
    yield xDai).provide(ZLayer.succeed(httpClient), Scope.default)

object GnosisScanAccountsClient:
  val layer: ZLayer[Client, Config.Error, GnosisScanAccountsClient] =
    ZLayer {
      for
        config <- ZIO.config(GnosisScanConfig.config)
        client <- ZIO.service[Client]
      yield GnosisScanAccountsClient(config, client)
    }
