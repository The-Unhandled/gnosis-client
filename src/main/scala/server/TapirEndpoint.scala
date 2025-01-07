package xyz.forsaken.gnosisclient
package server

import sttp.tapir.{AnyEndpoint, PublicEndpoint}
import sttp.tapir.ztapir.*
import zio.ZIO
import zio.http.{Response, Routes}


/** @author
  *   Petros Siatos
  */
trait TapirEndpoint:

  protected val baseEndpoint: PublicEndpoint[Unit, String, Unit, Any] =
    endpoint.errorOut(stringBody)

  def wrapLogic[R, I, O](
      endpoint: PublicEndpoint[I, String, O, Any],
      logic: I => ZIO[R, Throwable, O]
  ): ZServerEndpoint[R, Any] =
    endpoint.zServerLogic { input =>
      logic(input).mapError(_.getMessage)
    }

  def endpoints: List[AnyEndpoint]
  
  def routes: Routes[Any, Response]
