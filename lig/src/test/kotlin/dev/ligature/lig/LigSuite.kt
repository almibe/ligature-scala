/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import arrow.core.Either
import dev.ligature.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import dev.ligature.rakkoon.*

class LigSuite : FunSpec() {
    init {
        val ligParser = LigParser()
        val ligWriter = LigWriter()

        test("write entities") {
            val entity = Entity("test")
            val res = ligWriter.writeEntity(entity)
            res shouldBe "<test>"
        }

        test("parse entities") {
            val test = "<test>"
            val entity = ligParser.parseEntity(Rakkoon(test), null)
            entity shouldBe Either.Right(Entity("test"))
        }

        test("write attributes") {
            val attribute = Attribute("test")
            val res = ligWriter.writeAttribute(attribute)
            res shouldBe "@<test>"
        }

        test("parse attributes") {
            val test = "@<test>"
            val attribute = ligParser.parseAttribute(Rakkoon(test), null)
            attribute shouldBe Either.Right(Attribute("test"))
        }

        test("write FloatLiteral") {
            val test = FloatLiteral(3.0)
            val res = ligWriter.writeValue(test)
            res shouldBe "3.0"
        }

        test("parse FloatLiteral") {
            val test = "3.5"
            val res = ligParser.parseFloatLiteral(Rakkoon(test))
            res shouldBe Either.Right(FloatLiteral(3.5))
        }

        test("write IntegerLiteral") {
            val test = IntegerLiteral(3535)
            val res = ligWriter.writeValue(test)
            res shouldBe "3535"
        }

        test("parse IntegerLiteral") {
            val test = "3452345"
            val res = ligParser.parseIntegerLiteral(Rakkoon(test))
            res shouldBe Either.Right(IntegerLiteral(3452345))
        }

        test("write StringLiteral") {
            val test = StringLiteral("3535 55Hello")
            val res = ligWriter.writeValue(test)
            res shouldBe "\"3535 55Hello\""
        }

        test("parse StringLiteral") {
            val test = "\"3452345\\nHello\""
            val res = ligParser.parseStringLiteral(Rakkoon(test))
            res shouldBe Either.Right(StringLiteral("3452345\\nHello"))
        }

        test("basic Statement with all Entities") {
            val statement = Statement(Entity("e1"), Attribute("a1"), Entity("e2"), Entity("context"))
            val lines = ligWriter.write(listOf(statement).iterator())
            val resStatements = ligParser.parse(lines)
            listOf(statement) shouldBe resStatements.asSequence().toList()
        }

        test("list of Statements with Literal Values") {
            val statements = listOf(
                Statement(Entity("e1"), Attribute("a1"), Entity("e2"), Entity("context")),
                Statement(Entity("e2"), Attribute("a2"), StringLiteral("string literal"), Entity("context2")),
                Statement(Entity("e2"), Attribute("a3"), IntegerLiteral(Long.MAX_VALUE), Entity("context3")),
                Statement(Entity("e3"), Attribute("a4"), FloatLiteral(7.5), Entity("context4"))
            )
            val lines = ligWriter.write(statements.iterator())
            val resStatements = ligParser.parse(lines)
            statements shouldBe resStatements.asSequence().toList()
        }

//        test("parsing with wildcards") {
//            val textInput = "<e1> @<a2> 777 <e5>\n" +
//                    "_ @<a3> _ <e6>\n" +
//                    "_ _ \"Hello\" _\n" +
//                    "<e7> _ _ <e5>\n"
//            val expectedStatements = listOf(
//                Statement(Entity("e1"), Attribute("a2"), IntegerLiteral(777), Entity("e5")),
//                Statement(Entity("e1"), Attribute("a3"), IntegerLiteral(777), Entity("e6")),
//                Statement(Entity("e1"), Attribute("a3"), StringLiteral("Hello"), Entity("e6")),
//                Statement(Entity("e7"), Attribute("a3"), StringLiteral("Hello"), Entity("e5"))
//            )
//            val resStatements = ligParser.parse(textInput)
//            expectedStatements shouldBe resStatements.asSequence().toList()
//        }
    }
}
