package xyz.forsaken.gnosisclient
package ethereum

import zio.test.*
import AbiContract.*
import xyz.forsaken.gnosisclient.ethereum.AbiContract.ABI_IO_TYPE.ADDRESS

object AbiEncoderSpec extends ZIOSpecDefault:

  def spec = suite("abi")(
    test("encode function signature") {
      val input = "balanceOf(address)"
      val expectedOutput = "70a08231000000000000000000000000"
      val result = AbiEncoder.bytes4(input)
      assertTrue(result == expectedOutput)
    },
    test("encode") {
      val fun = function(
        name = "balanceOf",
        inputs = Set(AbiFunctionIO("", ADDRESS))
      )
      val expectedOutput = "0x70a08231000000000000000000000000xxxaddressxxx"

      val result = AbiEncoder.encode(fun, "xxxaddressxxx")
      assertTrue(result == expectedOutput)

    }
  )
