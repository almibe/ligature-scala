/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.{
  Identifier,
  IntegerLiteral,
  Statement,
  StringLiteral,
  Value
}

def write(statements: Iterator[Statement]): String = {
  val sb = StringBuilder()
  statements.foreach { statement =>
    sb.append(s"${writeStatement(statement)}\n")
  }
  return sb.toString()
}

def writeStatement(statement: Statement): String =
  StringBuilder()
    .append(writeIdentifier(statement.entity))
    .append(' ')
    .append(writeIdentifier(statement.attribute))
    .append(' ')
    .append(writeValue(statement.value))
    .toString()

def writeIdentifier(identifier: Identifier): String = s"<${identifier.name}>"

def writeValue(value: Value): String =
  value match {
    case id: Identifier        => writeIdentifier(id)
    case IntegerLiteral(value) => value.toString()
    case StringLiteral(value) =>
      s"\"${value}\"" // TODO this needs to handle escaping special characters
  }
