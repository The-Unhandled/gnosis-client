package xyz.forsaken.gnosisclient
package ethereum

import ethereum.AbiContract.*
import infra.EnumCodec.*

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

/** @author
  *   Petros Siatos
  *
  * @see
  *   [[https://docs.soliditylang.org/en/latest/abi-spec.html#json Solidity ABI Spec]]
  */
object AbiContract:

  sealed trait AbiType:
    def `type`: ABI_METHOD_TYPE

  // Jsoniter cannot derive method if it is capitalized.
  // Haven't found a way to override this. Custom codec in jsoniter is atrocious.
  final case class function(
      name: String,
      constant: Boolean,
      inputs: Set[AbiFunctionIO],
      outputs: Set[AbiFunctionIO],
      payable: Boolean,
      stateMutability: STATE_MUTABILITY
  ) extends AbiType {
    override val `type`: ABI_METHOD_TYPE = ABI_METHOD_TYPE.FUNCTION
  }

  object function:

    def apply(name: String, inputs: Set[AbiFunctionIO]): function =
      function(
        name = name,
        constant = false,
        inputs = inputs,
        outputs = Set.empty,
        payable = false,
        stateMutability = STATE_MUTABILITY.NONPAYABLE
      )

    given abiFunctionCodec: JsonValueCodec[function] = JsonCodecMaker.make

  final case class event(
      name: String,
      anonymous: Boolean,
      inputs: Set[AbiFunctionIO]
  ) extends AbiType {
    override val `type`: ABI_METHOD_TYPE = ABI_METHOD_TYPE.EVENT
  }

  object event:
    given codec: JsonValueCodec[event] = JsonCodecMaker.make

  final case class fallback(
      payable: Boolean,
      stateMutability: STATE_MUTABILITY
  ) extends AbiType {
    override val `type`: ABI_METHOD_TYPE = ABI_METHOD_TYPE.FALLBACK
  }

  object fallback:
    given codec: JsonValueCodec[fallback] = JsonCodecMaker.make

  enum ABI_METHOD_TYPE:
    case FUNCTION extends ABI_METHOD_TYPE
    case CONSTRUCTOR extends ABI_METHOD_TYPE
    case RECEIVE extends ABI_METHOD_TYPE
    case FALLBACK extends ABI_METHOD_TYPE
    case EVENT extends ABI_METHOD_TYPE
    case ERROR extends ABI_METHOD_TYPE

  enum STATE_MUTABILITY extends Enum[STATE_MUTABILITY]:
    case PURE extends STATE_MUTABILITY
    case VIEW extends STATE_MUTABILITY
    case PAYABLE extends STATE_MUTABILITY
    case NONPAYABLE extends STATE_MUTABILITY

  object STATE_MUTABILITY:
    given codec: JsonValueCodec[STATE_MUTABILITY] =
      createEnumCodec[STATE_MUTABILITY](classOf[STATE_MUTABILITY])

  case class AbiFunctionIO(name: String, `type`: ABI_IO_TYPE)

  enum ABI_IO_TYPE extends Enum[ABI_IO_TYPE]:
    case STRING extends ABI_IO_TYPE
    case ADDRESS extends ABI_IO_TYPE
    case BOOL extends ABI_IO_TYPE
    case UINT8 extends ABI_IO_TYPE
    case UINT256 extends ABI_IO_TYPE

  object ABI_IO_TYPE:
    given codec: JsonValueCodec[ABI_IO_TYPE] =
      createEnumCodec[ABI_IO_TYPE](classOf[ABI_IO_TYPE])

  given abiTypeSetCodec: JsonValueCodec[Set[AbiType]] = JsonCodecMaker.make

  given abiTypeCodec: JsonValueCodec[AbiType] =
    JsonCodecMaker.make(
      CodecMakerConfig
        .withDiscriminatorFieldName(Some("type"))
        .withRequireDiscriminatorFirst(false)
    )
