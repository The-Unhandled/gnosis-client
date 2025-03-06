package xyz.forsaken.gnosisclient
package binance

import binance.BinanceClient.BinancePriceResponse
import domain.Price

import com.github.plokhotnyuk.jsoniter_scala.core.*
import zio.*
import zio.http.Client
import zio.test.*

import java.time.Instant

object BinanceClientIntegrationSpec extends DefaultSpec:

  override def spec: Spec[TestEnvironment, Any] =
    suite("BinanceClientSpec")(
      test("getPrice should return the price of the trading pair") {
        val pair = "GNOUSDT"
        for {
          client <- ZIO.service[BinanceClient]
          price <- client.getPrice(pair)
        } yield assertTrue(price >= 0.0)
      }.provideLayer(Client.default >>> BinanceClient.layer), //@@ TestAspect.ignore

      test("decode price from JSON") {
        val json = """{"mins": 5, "price": "154.19425038", "closeTime": 1740673717954}"""
        val expected = BinancePriceResponse(5, Price(154.19425038), Instant.ofEpochMilli(1740673717954L))
        val decoded = readFromString[BinancePriceResponse](json)
        assertTrue(decoded == expected)
      }
    )
