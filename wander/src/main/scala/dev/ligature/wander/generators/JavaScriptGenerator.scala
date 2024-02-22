/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.generators

import dev.ligature.wander.Expression

def generateJavaScript(expressions: Seq[Expression]): String =
  val sb = java.lang.StringBuilder()
  expressions.foreach(expression => sb.append(writeExpression(expression)))
  sb.toString()

def writeExpression(expression: Expression): String =
  expression match
    case _ => ???
