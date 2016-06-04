package de.envisia.play.thrift.backend

import de.envisia.play.thrift.mustache.Dictionary
import de.envisia.play.thrift.mustache.Dictionary._
import de.envisia.play.thrift.ast.{Enum, Identifier}

trait EnumTemplate { self: TemplateGenerator =>
  def enumDict(
    namespace: Identifier,
    enum: Enum
  ): Dictionary =
    Dictionary(
      "package" -> genID(namespace),
      "EnumName" -> genID(enum.sid.toTitleCase),
      "docstring" -> v(enum.docstring.getOrElse("")),
      "values" -> v(enum.values.map { value =>
        Dictionary(
          "valuedocstring" -> v(value.docstring.getOrElse("")),
          "name" -> genID(value.sid),
          "originalName" -> v(value.sid.originalName),
          "unquotedNameLowerCase" -> v(value.sid.fullName.toLowerCase),
          "value" -> v(value.value.toString)
        )
      })
    )
}
