/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.Statement
import dev.ligature.StringLiteral
import dev.ligature.Value

fun write(statements: Iterator<Statement>): String {
  val sb = StringBuilder()
  statements.forEach { statement ->
    sb.append("${writeStatement(statement)}\n") //TODO use system's new-line
  }
  return sb.toString()
}

fun writeStatement(statement: Statement): String =
  StringBuilder()
    .append(writeIdentifier(statement.entity))
    .append(' ')
    .append(writeIdentifier(statement.attribute))
    .append(' ')
    .append(writeValue(statement.value))
    .toString()

fun writeIdentifier(identifier: Identifier): String = "<${identifier.name}>"

fun writeValue(value: Value): String =
  when(value) {
    is Identifier     -> writeIdentifier(value)
    is IntegerLiteral -> value.value.toString()
    is StringLiteral  ->
      "\"${value.value}\"" // TODO this needs to handle escaping special characters
  }
