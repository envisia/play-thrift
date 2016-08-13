package de.envisia.play.thrift.backend

import de.envisia.play.thrift.mustache.Dictionary.v
import de.envisia.play.thrift.mustache.Dictionary
import de.envisia.play.thrift.ast.{Identifier, ConstDefinition}

trait ConstsTemplate { self: TemplateGenerator =>
  def constDict(
    namespace: Identifier,
    consts: Seq[ConstDefinition]
  ): Dictionary = Dictionary(
    "package" -> genID(namespace),
    "constants" -> v(consts map {
      c =>
        Dictionary(
          "name" -> genID(c.sid),
          "fieldType" -> genType(c.fieldType),
          "value" -> genConstant(c.value, Some(c.fieldType)),
          "docstring" -> v(c.docstring.getOrElse(""))
        )
    })
  )
}
