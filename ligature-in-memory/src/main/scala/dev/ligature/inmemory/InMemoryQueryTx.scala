/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.inmemory

import dev.ligature.*
import io.smallrye.mutiny.{Multi, Uni}

/** Represents a QueryTx within the context of a Ligature instance and a single Dataset */
class InMemoryQueryTx(private val store: DatasetStore) extends QueryTx {
    /** Returns all PersistedStatements in this Dataset. */
    def allStatements(): Multi[Either[LigatureError, PersistedStatement]] = {
        ???
//        Stream.emits(store.statements.map(Right(_)).toSeq)
    }

    /** Returns all PersistedStatements that match the given criteria.
     * If a parameter is None then it matches all, so passing all Nones is the same as calling allStatements. */
    def matchStatements(
                         entity: Option[Entity],
                         attribute: Option[Attribute],
                         value: Option[Value],
                       ): Multi[Either[LigatureError, PersistedStatement]] = {
        ???
//        var res = Stream.emits(store.statements.toSeq)
//        if (entity.isDefined) {
//            res = res.filter(_.statement.entity == entity.get)
//        }
//        if (attribute.isDefined) {
//            res = res.filter(_.statement.attribute == attribute.get)
//        }
//        if (value.isDefined) {
//            res = res.filter(_.statement.value == value.get)
//        }
//        res.map(Right(_))
    }

    /** Retuns all PersistedStatements that match the given criteria.
     * If a parameter is None then it matches all. */
    def matchStatementsRange(
                              entity: Option[Entity],
                              attribute: Option[Attribute],
                              range: dev.ligature.Range,
                            ): Multi[Either[LigatureError, PersistedStatement]] = {
        ???
//        var res = Stream.emits(store.statements.toSeq)
//        if (entity.isDefined) {
//            res = res.filter(_.statement.entity == entity.get)
//        }
//        if (attribute.isDefined) {
//            res = res.filter(_.statement.attribute == attribute.get)
//        }
//        res = res.filter { ps =>
//            val testValue = ps.statement.value
//            (testValue, range) match {
//                case (StringLiteral(v), StringLiteralRange(start, end))     => v >= start && v < end
//                case (FloatLiteral(v), FloatLiteralRange(start, end))       => v >= start && v < end
//                case (IntegerLiteral(v), IntegerLiteralRange(start, end)) => v >= start && v < end
//                case _                                                      => false
//            }
//        }
//        res.map(Right(_))
    }

    /** Returns the PersistedStatement for the given context. */
    def statementForContext(context: Entity): Uni[Either[LigatureError, Option[PersistedStatement]]] = ???//IO {
//        Right(store.statements.find(_.context == context))
//    }
}
