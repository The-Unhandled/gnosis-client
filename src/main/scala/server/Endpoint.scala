package xyz.forsaken.gnosisclient
package server

import zio.http.Routes

/** @author
  *   Petros Siatos
  */
trait Endpoint:
  def routes: Routes[Any, Throwable]
