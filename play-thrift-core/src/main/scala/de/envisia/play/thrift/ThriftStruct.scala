package de.envisia.play.thrift

import org.apache.thrift.protocol.{ TProtocol, TType }

trait ThriftStruct {
  @throws(classOf[org.apache.thrift.TException])
  def write(oprot: TProtocol)
}

trait ThriftResponse[Result] {
  def successField: Option[Result]
  def exceptionFields: Iterable[Option[ThriftException]]
  /**
   * Return the first nonempty exception field.
   */
  def firstException(): Option[ThriftException] =
    exceptionFields.collectFirst(ThriftResponse.exceptionIsDefined)
}

object ThriftResponse {
  private val exceptionIsDefined: PartialFunction[Option[ThriftException], ThriftException] = {
    case Some(exception) => exception
  }
}

/**
 * Unions are tagged with this trait as well as with [[ThriftStruct]].
 */
trait ThriftUnion

/**
 * A trait encapsulating the logic for encoding and decoding a specific thrift struct
 * type.  The methods encoder and decoder are used by scrooge2 generated code, but are
 * now deprecated.  scrooge3 generated code uses encode and decode.
 */
trait ThriftStructCodec[T <: ThriftStruct] {
  @throws(classOf[org.apache.thrift.TException])
  def encode(t: T, oprot: TProtocol) = encoder(t, oprot)

  @throws(classOf[org.apache.thrift.TException])
  def decode(iprot: TProtocol): T = decoder(iprot)

  lazy val metaData: ThriftStructMetaData[T] = new ThriftStructMetaData(this)

  @deprecated("your code is generated by an outdated version of scrooge-generator", "2.7.0")
  def encoder: (T, TProtocol) => Unit

  @deprecated("your code is generated by an outdated version of scrooge-generator", "2.7.0")
  def decoder: TProtocol => T
}

/**
 * structs generated by scrooge3 will extends this base class, which provides implementations
 * for the deprecated encoder/decoder methods that depend on encode and decode, which are
 * generated.
 */
abstract class ThriftStructCodec3[T <: ThriftStruct] extends ThriftStructCodec[T] {
  override val encoder: (T, TProtocol) => Unit = encode
  override val decoder: TProtocol => T = decode

  protected def ttypeToString(byte: Byte): String = {
    // from https://github.com/apache/thrift/blob/master/lib/java/src/org/apache/thrift/protocol/TType.java
    byte match {
      case TType.STOP   => "STOP"
      case TType.VOID   => "VOID"
      case TType.BOOL   => "BOOL"
      case TType.BYTE   => "BYTE"
      case TType.DOUBLE => "DOUBLE"
      case TType.I16    => "I16"
      case TType.I32    => "I32"
      case TType.I64    => "I64"
      case TType.STRING => "STRING"
      case TType.STRUCT => "STRUCT"
      case TType.MAP    => "MAP"
      case TType.SET    => "SET"
      case TType.LIST   => "LIST"
      case TType.ENUM   => "ENUM"
      case _            => "UNKNOWN"
    }
  }

}

/**
 * Metadata for a thrift method.
 */
trait ThriftMethod {
  /** A struct wrapping method arguments */
  type Args <: ThriftStruct
  /** The successful return type */
  type SuccessType
  /** Contains success or thrift application exceptions */
  type Result <: ThriftResponse[SuccessType] with ThriftStruct

  /** Thrift method name */
  def name: String
  /** Thrift service name. A thrift service is a list of methods. */
  def serviceName: String
  /** Codec for the request args */
  def argsCodec: ThriftStructCodec3[Args]
  /** Codec for the response */
  def responseCodec: ThriftStructCodec3[Result]
  /** True for oneway thrift methods */
  def oneway: Boolean
}

