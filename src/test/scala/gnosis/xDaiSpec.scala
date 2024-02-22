package xyz.forsaken.gnosisclient
package gnosis

import zio.test.*

object xDaiSpec extends ZIOSpecDefault:

  def spec = suite("xDai")(
    test("extract value from Wei correctly") {
      val xdai = xDai.fromWei(4048424114301559002L)
      assertTrue(xdai.value == 4.05d)
    },
    
    test("extract value from hexadecimal") {
      
      val hex = "0x00000000000000000000000000000000000000000000000374181a4949a78000"
      
      val xdai = xDai.fromHexadecimal(hex)
      assertTrue(xdai.value == 63.71d)
    }
  )