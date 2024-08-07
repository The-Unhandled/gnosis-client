package xyz.forsaken.gnosisclient
package infra

import zio.test.ZIOSpecDefault

import java.net.URI
import zio.*
import zio.http.*
import zio.test.*

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

/** @author
  *   Petros Siatos
  */
object CommonHttpClientSpec extends ZIOSpecDefault:

  private val localhost = "http://localhost"

  val client: CommonHttpClient = new CommonHttpClient:
    override protected val uri: URI = URI.create(localhost)
    override protected val apiKey: String = "APIKEY"

  case class ResponseBody(status: String, message: String, result: String)

  given codec: JsonValueCodec[ResponseBody] = JsonCodecMaker.make

  def spec = suite("CommonHttpClient")(
    test("getUrl") {
      for url <- client.getUrl
      yield assertTrue(url.queryParams == QueryParams("apikey" -> "APIKEY"))
    },
    test("request") {

      val expectedResponse = ResponseBody("success", "message", "result")

      for {
        _ <- TestClient.addRoutes {
          Routes(
            Method.GET / "action" -> handler {
              Response.text(writeToString(expectedResponse))
            }
          )
        }
        responseBody <- client.request[ResponseBody](URL.root / "action")
      } yield assertTrue(responseBody == expectedResponse)
    }.provide(TestClient.layer, Scope.default)
  )
