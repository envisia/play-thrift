package {{package}}

import de.envisia.play.thrift._
import play.api.mvc.{ Action, ActionBuilder, Request }
import java.nio.ByteBuffer
import java.util.Arrays
import org.apache.thrift.protocol._
import org.apache.thrift.transport.TTransport
import org.apache.thrift.TApplicationException
import org.apache.thrift.transport.TMemoryBuffer
import scala.collection.immutable.{Map => immutable$Map}
import scala.collection.mutable
import scala.collection.{Map, Set}
import scala.language.higherKinds
import scala.concurrent.{ ExecutionContext, Future }


trait {{ServiceName}}Methods {

  {{#thriftFunctions}}
  object {{funcObjectName}} extends ThriftMethod {
  {{#functionArgsStruct}}
  {{>struct}}
  {{/functionArgsStruct}}

  type SuccessType = {{typeName}}
  {{#internalResultStruct}}
  {{>struct}}
  {{/internalResultStruct}}

  val name = "{{originalFuncName}}"
  val serviceName = "{{ServiceName}}"
  val argsCodec = Args
  val responseCodec = Result
  val oneway = {{is_oneway}}
  }

  // Compatibility aliases.
  val {{funcName}}$args = {{funcObjectName}}.Args
  type {{funcName}}$args = {{funcObjectName}}.Args

  val {{funcName}}$result = {{funcObjectName}}.Result
  type {{funcName}}$result = {{funcObjectName}}.Result

  {{/thriftFunctions}}

}

abstract class Abstract{{ServiceName}}(
    protocolFactory: TProtocolFactory,
    actionBuilder: ActionBuilder[Request]
)(implicit ec: ExecutionContext) extends ThriftController(protocolFactory, actionBuilder) with {{ServiceName}}Methods {

  def this(protocolFactory: TProtocolFactory)(implicit ec: ExecutionContext) {
    this(protocolFactory, Action)
  }

{{#functions}}
  {{>thriftServiceFunction}}
{{/function}}

{{#withFinagle}}
{{#asyncFunctions}}
  {{>function}}
{{/asyncFunctions}}
{{/withFinagle}}

}
