package xyz.forsaken.gnosisclient

import zio.http.*
import zio.test.*
import zio.test.Assertion.equalTo

/** @author
  *   Petros Siatos
  */
object Spec extends ZIOSpecDefault {

  def spec = suite("http")(
    test("should be ok") {
      val app = Handler.ok.toRoutes
      val req = Request.get(URL(Path.root))
      assertZIO(app.runZIO(req))(equalTo(Response.ok))
    }
  )
}
