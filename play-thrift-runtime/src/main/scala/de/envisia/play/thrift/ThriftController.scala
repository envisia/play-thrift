// Copyright (C) 2016 envisia GmbH
// All Rights Reserved.

package de.envisia.play.thrift


import java.util

import org.apache.thrift.TApplicationException
import org.apache.thrift.protocol.{ TMessageType, _ }
import org.apache.thrift.transport.TMemoryInputTransport
import play.api.mvc.{ Action, ActionBuilder, Controller, Request }
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.higherKinds

abstract class ThriftController(
    protocolFactory: TProtocolFactory,
    actionBuilder: ActionBuilder[Request]
)(implicit ec: ExecutionContext) extends Controller with SimpleRouter {

  def this(protocolFactory: TProtocolFactory) = {
    this(protocolFactory, Action)
  }

  private[this] val tlReusableBuffer = new ThreadLocal[TReusableMemoryTransport] {
    override def initialValue() = TReusableMemoryTransport(512)
  }

  private[this] def reusableBuffer: TReusableMemoryTransport = {
    val buf = tlReusableBuffer.get()
    buf.reset()
    buf
  }

  private[this] def resetBuffer(trans: TReusableMemoryTransport): Unit = {
    if (trans.currentCapacity > 16 * 1024) {
      tlReusableBuffer.remove()
    }
  }

  protected val functionMap = scala.collection.mutable.HashMap[String, (TProtocol, Int) => Future[Array[Byte]]]()

  protected def addFunction(name: String, f: (TProtocol, Int) => Future[Array[Byte]]) {
    functionMap(name) = f
  }

  protected def reply(name: String, seqid: Int, result: ThriftStruct): Future[Array[Byte]] = {
    try {
      val memoryBuffer = reusableBuffer
      try {
        val oprot = protocolFactory.getProtocol(memoryBuffer)

        oprot.writeMessageBegin(new TMessage(name, TMessageType.REPLY, seqid))
        result.write(oprot)
        oprot.writeMessageEnd()

        Future.successful(util.Arrays.copyOfRange(memoryBuffer.getArray(), 0, memoryBuffer.length()))
      } finally {
        resetBuffer(memoryBuffer)
      }
    } catch {
      case e: Exception => Future.failed(e)
    }
  }


  private[this] def route = actionBuilder.async(parse.raw) { implicit request =>
    val send = request.body.asBytes().map(_.toByteBuffer.array()) match {
      case Some(data) =>
        try {
          val inputTransport = new TMemoryInputTransport(data)
          val iprot = protocolFactory.getProtocol(inputTransport)

          val msg = iprot.readMessageBegin()
          val func = functionMap.get(msg.name)
          func match {
            case Some(fn) => fn(iprot, msg.seqid)
            case _ => TProtocolUtil.skip(iprot, TType.STRUCT)
              exception(msg.name, msg.seqid, TApplicationException.UNKNOWN_METHOD,
                "Invalid method name: '" + msg.name + "'")
          }
        } catch {
          case e: Exception => Future.failed(e)
        }

      case None => Future.failed(throw new IllegalStateException("not a valid state since the byte buffer should be filled!"))
    }

    send.map(Ok(_).as("application/x-thrift"))
  }

  protected def exception(name: String, seqid: Int, code: Int, message: String): Future[Array[Byte]] = {
    try {
      val x = new TApplicationException(code, message)
      val memoryBuffer = reusableBuffer
      try {
        val oprot = protocolFactory.getProtocol(memoryBuffer)

        oprot.writeMessageBegin(new TMessage(name, TMessageType.EXCEPTION, seqid))
        x.write(oprot)
        oprot.writeMessageEnd()
        oprot.getTransport.flush()
        Future.successful(util.Arrays.copyOfRange(memoryBuffer.getArray(), 0, memoryBuffer.length()))
      } finally {
        // resetBuffer(memoryBuffer)
      }
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  override def routes: Routes = {
    case POST(p"/") => this.route
  }

}
