package xyz.forsaken.gnosisclient
package ethereum

import blockscout.BlockscoutClient.TokenBalancesResponse
import domain.Balance

import com.github.plokhotnyuk.jsoniter_scala.core.readFromString

import scala.io.Source
import zio.test.*
import ethereum.AbiContract.*

import xyz.forsaken.gnosisclient.ethereum.AbiContract.*

/** @author
  *   Petros Siatos
  */
object AbiContractSpec extends ZIOSpecDefault:
  def spec = suite("AbiContract")(
    test("decode correctly") {
      val contractjson =
        Source.fromResource("wxdaiContract.json").getLines().mkString

      val contracts = readFromString[Set[AbiType]](contractjson)
      val expectedFunction = function(
        constant = false,
        name = "approve",
        inputs = Set(
          AbiFunctionIO(name = "guy", `type` = ABI_IO_TYPE.ADDRESS),
          AbiFunctionIO(name = "wad", `type` = ABI_IO_TYPE.UINT256)
        ),
        outputs = Set(
          AbiFunctionIO(name = "", `type` = ABI_IO_TYPE.BOOL)
        ),
        payable = false,
        stateMutability = STATE_MUTABILITY.NONPAYABLE
      )
      
      assertTrue(
        contracts.contains(expectedFunction)
      )
    }
  )
