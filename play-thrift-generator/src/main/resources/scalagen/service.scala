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


abstract class Abstract{{ServiceName}}(
    protocolFactory: TProtocolFactory,
    actionBuilder: ActionBuilder[Request]
)(implicit ec: ExecutionContext) extends ThriftController(protocolFactory, actionBuilder) {

  def this(protocolFactory: TProtocolFactory) {
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
