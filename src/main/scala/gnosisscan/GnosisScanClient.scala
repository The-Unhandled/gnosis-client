package xyz.forsaken.gnosisclient
package gnosisscan

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import zio.http.*

/** @author
  *   Petros Siatos
  */
trait GnosisScanClient:

  import GnosisScanClient.*

  protected val module: Module
  protected val config: GnosisScanConfig

  protected def queryParams(action: String) = QueryParams(
    "module" -> module.toString,
    "action" -> action,
    "tag" -> "latest",
    "apikey" -> config.apiKey
  )

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
