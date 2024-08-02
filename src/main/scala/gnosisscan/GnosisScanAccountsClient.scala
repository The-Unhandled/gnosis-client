package xyz.forsaken.gnosisclient
package gnosisscan

import domain.AccountsClient
import domain.gnosis.xDai
import gnosisscan.GnosisScanClient.Module.Account

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.*
import zio.http.*

import javax.naming.ConfigurationException

final class GnosisScanAccountsClient(
    val config: GnosisScanConfig,
    httpClient: Client
) extends GnosisScanClient
    with AccountsClient:

  final protected val module = Account

  override def getxDaiBalance(address: String): Task[xDai] =

    // FIXME:
    val url: URL = URL
      .fromURI(config.url)
      .getOrElse(throw new ConfigurationException("Invalid URL"))
      .queryParams(
        queryParams("balance") ++ QueryParams("address" -> address)
      )

    (for
      response <- httpClient.url(url).get("/")
      responseBody <- response.body.asString
      xDai <- ZIO
        .attempt(
          readFromString[ResponseBody](responseBody)
        )
        .map(x => xDai.fromWei(BigDecimal(x.result)))
    yield xDai).provideSomeLayer(Scope.default)

object GnosisScanAccountsClient:
  val layer: ZLayer[Client, Config.Error, GnosisScanAccountsClient] =
    ZLayer {
      for
        config <- ZIO.config(GnosisScanConfig.config)
        client <- ZIO.service[Client]
      yield GnosisScanAccountsClient(config, client)
    }
