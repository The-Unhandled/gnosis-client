package xyz.forsaken.gnosisclient
package ethereum

import ethereum.AbiContract.*
import infra.EnumCodec.*

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

/** @author
  *   Petros Siatos
  * @see
  *   [[https://docs.soliditylang.org/en/latest/abi-spec.html#json Solidity ABI Spec]]
  */
object AbiContract:

  sealed trait AbiType:
    def `type`: ABI_METHOD_TYPE

  final case class Function(
      constant: Boolean,
      inputs: Set[AbiFunctionIO],
      name: String,
      outputs: Set[AbiFunctionIO],
      payable: Boolean,
      stateMutability: STATE_MUTABILITY
  ) extends AbiType {
    override val `type`: ABI_METHOD_TYPE = ABI_METHOD_TYPE.FUNCTION
  }

  object Function:
    given abiFunctionCodec: JsonValueCodec[Function] = JsonCodecMaker.make

  enum ABI_METHOD_TYPE:
    case FUNCTION extends ABI_METHOD_TYPE
    case CONSTRUCTOR extends ABI_METHOD_TYPE
    case RECEIVE extends ABI_METHOD_TYPE
    case FALLBACK extends ABI_METHOD_TYPE
  

  enum STATE_MUTABILITY extends Enum[STATE_MUTABILITY]:
    case PURE extends STATE_MUTABILITY
    case VIEW extends STATE_MUTABILITY
    case PAYABLE extends STATE_MUTABILITY
    case NONPAYABLE extends STATE_MUTABILITY

  object STATE_MUTABILITY:
    given codec: JsonValueCodec[STATE_MUTABILITY] = createEnumCodec[STATE_MUTABILITY](classOf[STATE_MUTABILITY])

  case class AbiFunctionIO(name: String, `type`: ABI_IO_TYPE)

  enum ABI_IO_TYPE extends Enum[ABI_IO_TYPE]:
    case STRING extends ABI_IO_TYPE
    case ADDRESS extends ABI_IO_TYPE
    case BOOL extends ABI_IO_TYPE
    case UINT8 extends ABI_IO_TYPE
    case UINT256 extends ABI_IO_TYPE

  object ABI_IO_TYPE:
    given codec: JsonValueCodec[ABI_IO_TYPE] = createEnumCodec[ABI_IO_TYPE](classOf[ABI_IO_TYPE])


  given abiTypeCodec: JsonValueCodec[AbiType] =
    JsonCodecMaker.make(
      CodecMakerConfig
        .withDiscriminatorFieldName(Some("type"))
        .withRequireDiscriminatorFirst(false)
    )
