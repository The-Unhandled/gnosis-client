package xyz.forsaken.gnosisclient
package ethereum

import org.bouncycastle.jcajce.provider.digest.Keccak
import AbiContract.*

/**
 * @author Petros Siatos
 */
object AbiEncoder:

  final val keccak = new Keccak.Digest256

  def encode(function: function, input: String): String =
    val encodedFunctionSignature = bytes4(function.signature)
    val encodedInputValues = input
    s"0x$encodedFunctionSignature$encodedInputValues"

  implicit class functionOps(f: function):
    // TODO: encode input ABI types
    def signature: String = s"${f.name}(${f.inputs.map(_.`type`.toString.toLowerCase).mkString(",")})"

  //implicit class inputOps(i: AbiFunctionIO):

  private[ethereum] def bytes4(string: String): String =
    encode(string).take(8) + "000000000000000000000000"

  private[ethereum] def encode(string: String): String =
    val hashBytes = keccak.digest(string.getBytes())
    bytesToHex(hashBytes)

  private def bytesToHex(bytes: Array[Byte]): String = bytes.map("%02x".format(_)).mkString



