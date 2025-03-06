package xyz.forsaken.gnosisclient
package cowswap

import zio.*
import zio.http.*
import zio.test.*

object CowSwapIntegrationSpec extends DefaultSpec:

  override def spec: Spec[TestEnvironment, Any] =
    suite("CowSwapClient Integration Test")(
      test("Fetch quote for GNO/USDT from CowSwap API") {
        for {
          client <- ZIO.service[CowSwapClient]
          requestBody = CowSwapClient.CowSwapQuoteRequestBody(
            sellToken = "0xaf204776c7245bf4147c2612bf6e5972ee483701",
            buyToken = "0x177127622c4a00f3d409b75571e12cb3c8973d3c",
            from = "0xreceiver",
            receiver = "0xreceiver",
            appData = """{"appCode":"CoW Swap","environment":"production","metadata":{"orderClass":{"orderClass":"market"},"partnerFee":{"bps":10,"recipient":"0xrecepient"},"quote":{"slippageBips":50,"smartSlippage":false}},"version":"1.3.0"}""",
            appDataHash = "0x7cbd496aaa58a608cc00193f812074f58c3b2eaab1e3d20cd15915318fc1e218",
            partiallyFillable = false,
            priceQuality = "fast",
            validFor = 1800,
            kind = "sell",
            sellAmountBeforeFee = "8600000000000000000000"
          )
          quote <- client.getQuote(requestBody)
          _ <- ZIO.logInfo(s"Quote: $quote")
        } yield assertTrue(quote > 0.0)
      }
    ).provide(
      CowSwapClient.layer,
      Client.default
    )
