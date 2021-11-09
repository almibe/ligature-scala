/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import cats.effect.unsafe.implicits.global
import cats.effect.IO
import dev.ligature._
import munit._
import cats.effect.std.Console
import cats.effect.kernel.Ref

abstract class LigatureTestSuite extends CatsEffectSuite {
    def createLigature: Ligature

    val testDataset = Dataset.fromString("test/test").getOrElse(???)
    val testDataset2 = Dataset.fromString("test/test2").getOrElse(???)
    val testDataset3 = Dataset.fromString("test3/test").getOrElse(???)
    val a = Identifier.fromString("a").getOrElse(???)
    val b = Identifier.fromString("b").getOrElse(???)
    val entity1 = Identifier.fromString("a").getOrElse(???)
    val entity2 = Identifier.fromString("b").getOrElse(???)
    val entity3 = Identifier.fromString("c").getOrElse(???)
    val context1 = Identifier.fromString("context1").getOrElse(???)
    val context2 = Identifier.fromString("context2").getOrElse(???)
    val context3 = Identifier.fromString("context3").getOrElse(???)

    test("create and close store") {
        val instance = createLigature
        assertIO(instance.allDatasets().compile.toList, List())
    }

    test("creating a new dataset") {
        val instance = createLigature
        val res = for {
            _ <- instance.createDataset(testDataset)
            res <- instance.allDatasets().compile.toList
        } yield res
        assertIO(res, List(testDataset))
    }

    test("check if datasets exist") {
        val instance = createLigature
        val res = for {
            _ <- instance.createDataset(testDataset)
            exists1 <- instance.datasetExists(testDataset)
            exists2 <- instance.datasetExists(testDataset2)
        } yield (exists1, exists2)
        assertIO(res, (true, false))
    }

    test("match datasets prefix") {
        val instance = createLigature
        val res = for {
            _ <- instance.createDataset(testDataset)
            _ <- instance.createDataset(testDataset2)
            _ <- instance.createDataset(testDataset3)
            res1 <- instance.matchDatasetsPrefix("test").compile.toList
            res2 <- instance.matchDatasetsPrefix("test/").compile.toList
            res3 <- instance.matchDatasetsPrefix("snoo").compile.toList
        } yield (res1.length, res2.length, res3.length)
        assertIO(res, (3, 2 ,0))
    }

    test("match datasets range") {
        val instance = createLigature
        val res = for {
            _ <- instance.createDataset(Dataset.fromString("a").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("app").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("b").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("be").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("bee").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("test1/test").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("test2/test2").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("test3/test").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("test4").getOrElse(???))
            _ <- instance.createDataset(Dataset.fromString("z").getOrElse(???))
            res <- instance.allDatasets().compile.toList
            res1 <- instance.matchDatasetsRange("a", "b").compile.toList
            res2 <- instance.matchDatasetsRange("be", "test3").compile.toList
            res3 <- instance.matchDatasetsRange("snoo", "zz").compile.toList
        } yield (res1.length, res2.length, res3.length)
        assertIO(res, (2, 4, 5)) //TODO check instances not just counts
    }

    test("create and delete new dataset") {
        val instance = createLigature
        val res = for {
            _ <- instance.createDataset(testDataset)
            _ <- instance.deleteDataset(testDataset)
            _ <- instance.deleteDataset(testDataset2)
            res <- instance.allDatasets().compile.toList
        } yield res
        assertIO(res, List())
    }

    test("new datasets should be empty") {
        val instance = createLigature
        val res = for {
            _ <- instance.createDataset(testDataset)
            res <- instance.query(testDataset).use(tx => tx.allStatements().compile.toList)
        } yield res
        assertIO(res, List())
    }

    test("adding statements to datasets") {
        val instance = createLigature
        val res = for {
            _ <- instance.createDataset(testDataset)
            _ <- instance.write(testDataset).use { tx =>
                for {
                    _  <- tx.addStatement(Statement(entity1, a, entity2, context1))
                    _  <- tx.addStatement(Statement(entity1, a, entity2, context2)) //dupes get added since they'll have unique contexts
                    r  <- tx.addStatement(Statement(entity1, a, entity3, context3))
                } yield r
            }
            statements  <- instance.query(testDataset).use { tx =>
                tx.allStatements().compile.toList
            }
        } yield statements.toSet
        assertIO(res, Set(
            Statement(entity1, a, entity2, context1),
            Statement(entity1, a, entity2, context2),
            Statement(entity1, a, entity3, context3)))
    }

    test("new identifiers") {
        val instance = createLigature
        val res = for {
            _ <- instance.createDataset(testDataset)
            _ <- instance.write(testDataset).use { tx =>
                for {
                    entity <- tx.newIdentifier("entity-")
                    attribute <- tx.newIdentifier("attribute-")
                    value <- tx.newIdentifier("value-")
                    context <- tx.newIdentifier("context-")
                    ps1  <- tx.addStatement(Statement(entity, attribute, value, context))
                } yield IO(())
            }
            statements  <- instance.query(testDataset).use { tx =>
                tx.allStatements().compile.toList
            }
        } yield statements.head
        res.map(it => {
            assert(it.entity.name.startsWith("entity-"))
            assert(it.attribute.name.startsWith("attribute-"))
            it.value match {
                case Identifier(id) => assert(id.startsWith("value-"))
                case _ => assert(false)
            }
            assert(it.context.name.startsWith("context-"))
        })
    }

    test("removing statements from datasets") {
        val res = createLigature.instance.use { instance =>
            for {
                _ <- instance.createDataset(testDataset)
                ps2 <- instance.write(testDataset).use { tx =>
                    for {
                        ps1 <- tx.addStatement(Statement(entity1, a, entity2))
                        ps2 <- tx.addStatement(Statement(entity3, a, entity2))
                        _ <- tx.removeStatement(ps1.right.get)
                        _ <- tx.removeStatement(ps1.right.get)
                        //below doesn't actually remove since context is different
                        _ <- tx.removeStatement(PersistedStatement(Statement(entity3, a, entity2), entity1))
                    } yield ps2
                }
                statements <- instance.query(testDataset).use { tx =>
                    tx.allStatements().compile.toList
                }
            } yield (ps2, statements)
        }.unsafeRunSync()
        assertEquals(res._2.toSet, Set(res._1))
    }

    // test("get persisted statement from context") {
    //     val res = createLigature.instance.use { instance =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             ps <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     _ <- tx.addStatement(Statement(entity1, a, entity2))
    //                     ps <- tx.addStatement(Statement(entity2, a, entity3))
    //                 } yield ps.right.get
    //             }
    //             res <- instance.query(testDataset).use { tx =>
    //                 tx.statementForContext(ps.context)
    //             }
    //         } yield res
    //     }.unsafeRunSync().map(_.get.statement)
    //     assertEquals(res.right.get, Statement(entity2, a, entity3))
    // }

    // test("allow canceling WriteTx") {
    //     val res = createLigature.instance.use { instance  =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             _ <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     _ <- tx.addStatement(Statement(entity1, a, entity2))
    //                     _ <- tx.cancel()
    //                 } yield ()
    //             }
    //             _ <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     _ <- tx.addStatement(Statement(entity2, a, entity3))
    //                     _ <- tx.addStatement(Statement(entity3, a, entity2))
    //                 } yield ()
    //             }
    //             statements <- instance.query(testDataset).use { tx =>
    //                 tx.allStatements().compile.toList
    //             }
    //         } yield statements
    //     }.unsafeRunSync().map(_.right.get).map(_.statement).toSet
    //     assertEquals(res, Set(
    //         Statement(entity2, a, entity3),
    //         Statement(entity3, a, entity2)))
    // }

    // test("matching statements in datasets") {
    //     val (all, as, hellos, helloa) = createLigature.instance.use { instance  =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             _ <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     _    <- tx.addStatement(Statement(entity1, a, StringLiteral("Hello")))
    //                     _    <- tx.addStatement(Statement(entity2, a, entity1))
    //                     _    <- tx.addStatement(Statement(entity2, a, entity3))
    //                     _    <- tx.addStatement(Statement(entity3, b, entity2))
    //                     _    <- tx.addStatement(Statement(entity3, b, StringLiteral("Hello")))
    //                 } yield()
    //             }
    //             res <- instance.query(testDataset).use { tx =>
    //                 for {
    //                     all <- tx.matchStatements(None, None, None).compile.toList
    //                     as  <- tx.matchStatements(None, Some(a), None).compile.toList
    //                     hellos <- tx.matchStatements(None, None, Some(StringLiteral("Hello"))).compile.toList
    //                     helloa <- tx.matchStatements(None, Some(a), Some(StringLiteral("Hello"))).compile.toList
    //                 } yield (all, as, hellos, helloa)
    //             }
    //         } yield res
    //     }.unsafeRunSync()
    //     assertEquals(all.map(_.right.get).toSet.size, 5)
    //     assertEquals(as.map(_.right.get).toSet.size, 3)
    //     assertEquals(hellos.map(_.right.get).toSet.size, 2)
    //     assertEquals(helloa.map(_.right.get).toSet.size, 1)
    // }

    // test("matching statements with literals and ranges in datasets") {
    //     val (res1, res2, res3) = createLigature.instance.use { instance  =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             _ <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     _    <- tx.addStatement(Statement(entity1, a, entity2))
    //                     _    <- tx.addStatement(Statement(entity1, b, FloatLiteral(1.1)))
    //                     _    <- tx.addStatement(Statement(entity1, a, IntegerLiteral(5L)))
    //                     _    <- tx.addStatement(Statement(entity2, a, IntegerLiteral(3L)))
    //                     _    <- tx.addStatement(Statement(entity2, a, FloatLiteral(10.0)))
    //                     _    <- tx.addStatement(Statement(entity2, b, entity3))
    //                     _    <- tx.addStatement(Statement(entity3, a, IntegerLiteral(7L)))
    //                     _    <- tx.addStatement(Statement(entity3, b, FloatLiteral(12.5)))
    //                 } yield()
    //             }
    //             res <- instance.query(testDataset).use { tx =>
    //                 for {
    //                     res1 <- tx.matchStatementsRange(None, None, FloatLiteralRange(1.0, 11.0)).compile.toList
    //                     res2  <- tx.matchStatementsRange(None, None, IntegerLiteralRange(3,5)).compile.toList
    //                     res3 <- tx.matchStatementsRange(None, Some(b), FloatLiteralRange(1.0, 11.0)).compile.toList
    //                 } yield (res1, res2, res3)
    //             }
    //         } yield res
    //     }.unsafeRunSync()
    //     assertEquals(res1.map(_.right.get).toSet.size, 2)
    //     assertEquals(res2.map(_.right.get).toSet.size, 1)
    //     assertEquals(res3.map(_.right.get).toSet.size, 1)
    // }
}
