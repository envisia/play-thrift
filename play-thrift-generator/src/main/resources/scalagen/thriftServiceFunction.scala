addFunction("{{serviceFuncNameForWire}}", { (iprot: TProtocol, seqid: Int) =>
  try {
    val args = {{funcObjectName}}.Args.decode(iprot)
    iprot.readMessageEnd()
    (try {
      {{serviceFuncNameForCompile}}({{argNames}})
    } catch {
      case e: Exception => Future.failed(e)
    }).flatMap { value: {{typeName}} =>
      reply("{{serviceFuncNameForWire}}", seqid, {{funcObjectName}}.Result({{resultNamedArg}}))
    }.recoverWith {
{{#exceptions}}
      case e: {{exceptionType}} => {
        reply("{{serviceFuncNameForWire}}", seqid, {{funcObjectName}}.Result({{fieldName}} = Some(e)))
      }
{{/exceptions}}
      case e => Future.failed(e)
    }.andThen {
      case scala.util.Success(_) => // maybe add some kind of logging / stats
      case scala.util.Failure(ex) => // maybe add some kind of logging / stats
    }
  } catch {
    case e: TProtocolException => {
      iprot.readMessageEnd()
      exception("{{serviceFuncNameForWire}}", seqid, TApplicationException.PROTOCOL_ERROR, e.getMessage)
    }
    case e: Exception => Future.failed(e)
  }
})
