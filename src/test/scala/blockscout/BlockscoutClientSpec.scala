package xyz.forsaken.gnosisclient
package blockscout

import com.github.plokhotnyuk.jsoniter_scala.core.readFromString
import xyz.forsaken.gnosisclient.blockscout.BlockscoutClient.TokenResponse
import zio.test.ZIOSpecDefault

import scala.io.Source
import zio.test.*

import BlockscoutClient.*
import domain.Balance

/**
 * @author Petros Siatos
 */
object BlockscoutClientSpec extends ZIOSpecDefault:
  def spec = suite("BlockscoutClient")(
    
    
    test("decode correctly") {
      val tokens =  Source.fromResource("tokensResponse.json").getLines().mkString
            
      val result = readFromString[Set[TokenBalancesResponse]](tokens)
      
      assertTrue(result.exists(tbr => tbr.token.symbol.contains("WXDAI") && tbr.value == Balance(31.31)))
      assertTrue(result.exists(tbr => tbr.token.symbol.contains("WETH") && tbr.value == Balance(0.0)))
    }
  )
