package xyz.forsaken.gnosisclient
package gnosisscan

import infra.CommonHttpClient

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.Task
import zio.http.*

/** @author
  *   Petros Siatos
  */
trait GnosisScanClient extends CommonHttpClient:
  
  import GnosisScanClient.*

  protected val module: Module
  protected val config: GnosisScanConfig
  
  final val uri = config.url
  final val apiKey = config.apiKey

  private def queryParams(action: String) = QueryParams(
    "module" -> module.toString,
    "action" -> action,
    "tag" -> "latest",
    "apikey" -> config.apiKey
  )

  def getUrl(action: String): Task[URL] = for {
    url <- super.getUrl
    urlWithQueryParams = url.addQueryParams(queryParams(action))
  } yield urlWithQueryParams

  final case class ResponseBody(
      status: String,
      message: String,
      result: String
  )

  object ResponseBody:
    given codec: JsonValueCodec[ResponseBody] = JsonCodecMaker.make

  final case class JsonRpcResponseBody(
      jsonrpc: String,
      result: String,
      id: Int
  )

  object JsonRpcResponseBody:
    given codec: JsonValueCodec[JsonRpcResponseBody] = JsonCodecMaker.make

object GnosisScanClient:

  enum Module:
    case Account, Contract, Proxy

    override def toString: String = this match
      case Account  => "account"
      case Contract => "contract"
      case Proxy    => "proxy"
