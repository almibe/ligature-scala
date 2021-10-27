/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import munit.FunSuite
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral}
import dev.ligature.gaze.Gaze

class LigSpec extends FunSuite {
    val ligReader = LigReader()
    val testIdentifier = Identifier.fromString("test").getOrElse { ??? }
    def identifier(id: String) = Identifier.fromString(id).getOrElse { ??? }

    test("write identifiers") {
        val res = writeIdentifier(testIdentifier)
        assertEquals(res, "<test>")
    }

    test("parse identifiers") {
        val test = "<test>"
        val identifier = ligReader.parseIdentifier(Gaze.from(test))
        assertEquals(identifier, Right(testIdentifier))
    }

    test("complex entity identifier") {
        val identifierS = "<http$://&&this@2]34.[42;342?#--__>"
        val identifierRes = ligReader.parseIdentifier(Gaze.from(identifierS))
        assertEquals(identifierRes, Right(identifier("http$://&&this@2]34.[42;342?#--__")))
    }

    test("write IntegerLiteral") {
        val test = IntegerLiteral(3535)
        val res = writeValue(test)
        assertEquals(res, "3535")
    }

    test("parse IntegerLiteral") {
        val test = "3452345"
        val res = ligReader.parseIntegerLiteral(Gaze.from(test))
        assertEquals(res, Right(IntegerLiteral(3452345)))
    }

    test("write StringLiteral") {
        val test = StringLiteral("3535 55Hello")
        val res = writeValue(test)
        assertEquals(res, "\"3535 55Hello\"")
    }

    test("parse StringLiteral") {
        val test = "\"3452345\\nHello\""
        val res = ligReader.parseStringLiteral(Gaze.from(test))
        assertEquals(res, Right(StringLiteral("3452345\\nHello")))
    }

    test("basic Statement with all Entities") {
        val statement = Statement(identifier("e1"), identifier("a1"), identifier("e2"), identifier("context"))
        val lines = write(List(statement).iterator)
        val resStatements = ligReader.parse(lines)
        assertEquals(List(statement), resStatements.toList)
    }

    test("list of Statements with Literal Values") {
        val statements = List(
            Statement(identifier("e1"), identifier("a1"), identifier("e2"), identifier("context")),
            Statement(identifier("e2"), identifier("a2"), StringLiteral("string literal"), identifier("context2")),
            Statement(identifier("e2"), identifier("a3"), IntegerLiteral(Long.MaxValue), identifier("context3")),
            //Statement(identifier("e3"), identifier("a4"), FloatLiteral(7.5), identifier("context4"))
        )
        val lines = write(statements.iterator)
        val resStatements = ligReader.parse(lines)
        assertEquals(statements, resStatements.toList)
    }
}
