package xyz.forsaken.gnosisclient
package gnosisscan

import zio.http.Routes

/** @author
  *   Petros Siatos
  */
trait Endpoint:
  def routes: Routes[Any, Throwable]
