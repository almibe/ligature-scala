/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import cats.effect.unsafe.implicits.global
import dev.ligature._
import munit._

abstract class LigatureTestSuite extends FunSuite {
    def createLigature: Ligature

    val testDataset = Dataset.fromString("test/test").getOrElse(???)
    val testDataset2 = Dataset.fromString("test/test2").getOrElse(???)
    val testDataset3 = Dataset.fromString("test3/test").getOrElse(???)
    val a = Identifier.fromString("a").getOrElse(???)
    val b = Identifier.fromString("b").getOrElse(???)
    val entity1 = Identifier.fromString("a").getOrElse(???)
    val entity2 = Identifier.fromString("b").getOrElse(???)
    val entity3 = Identifier.fromString("c").getOrElse(???)

    test("create and close store") {
        val ligature = createLigature
        val res = ligature.allDatasets().compile.toList.unsafeRunSync()
        assert(res.isEmpty)
    }

    // test("creating a new dataset") {
    //     val res = createLigature.instance.use { (instance: LigatureInstance) =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             res <- instance.allDatasets().compile.toList
    //         } yield res
    //     }.unsafeRunSync()
    //     assertEquals(res, List(Right(testDataset)))
    // }

    // test("check if datasets exist") {
    //     val res = createLigature.instance.use { (instance: LigatureInstance) =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             exists1 <- instance.datasetExists(testDataset)
    //             exists2 <- instance.datasetExists(testDataset2)
    //         } yield (exists1, exists2)
    //     }.unsafeRunSync()
    //     assert(res._1.right.get)
    //     assert(!res._2.right.get)
    // }

    // test("match datasets prefix") {
    //     val res = createLigature.instance.use { (instance: LigatureInstance) =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             _ <- instance.createDataset(testDataset2)
    //             _ <- instance.createDataset(testDataset3)
    //             res1 <- instance.matchDatasetsPrefix("test").compile.toList
    //             res2 <- instance.matchDatasetsPrefix("test/").compile.toList
    //             res3 <- instance.matchDatasetsPrefix("snoo").compile.toList
    //         } yield (res1, res2, res3)
    //     }.unsafeRunSync()
    //     assertEquals(res._1.length, 3)
    //     assertEquals(res._2.length, 2)
    //     assertEquals(res._3.length, 0)
    // }

    // test("match datasets range") {
    //     val res = createLigature.instance.use { (instance: LigatureInstance) =>
    //         for {
    //             _ <- instance.createDataset(Dataset.fromString("a").get)
    //             _ <- instance.createDataset(Dataset.fromString("app").get)
    //             _ <- instance.createDataset(Dataset.fromString("b").get)
    //             _ <- instance.createDataset(Dataset.fromString("be").get)
    //             _ <- instance.createDataset(Dataset.fromString("bee").get)
    //             _ <- instance.createDataset(Dataset.fromString("test1/test").get)
    //             _ <- instance.createDataset(Dataset.fromString("test2/test2").get)
    //             _ <- instance.createDataset(Dataset.fromString("test3/test").get)
    //             _ <- instance.createDataset(Dataset.fromString("test4").get)
    //             _ <- instance.createDataset(Dataset.fromString("z").get)
    //             res <- instance.allDatasets().compile.toList
    //             res1 <- instance.matchDatasetsRange("a", "b").compile.toList
    //             res2 <- instance.matchDatasetsRange("be", "test3").compile.toList
    //             res3 <- instance.matchDatasetsRange("snoo", "zz").compile.toList
    //         } yield (res1, res2, res3)
    //     }.unsafeRunSync()
    //     assertEquals(res._1.length, 2) //TODO check instances not just counts
    //     assertEquals(res._2.length, 4) //TODO check instances not just counts
    //     assertEquals(res._3.length, 5) //TODO check instances not just counts
    // }

    // test("create and delete new dataset") {
    //     val res = createLigature.instance.use { instance  =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             _ <- instance.deleteDataset(testDataset)
    //             _ <- instance.deleteDataset(testDataset2)
    //             res <- instance.allDatasets().compile.toList
    //         } yield res
    //     }.unsafeRunSync()
    //     assert(res.isEmpty)
    // }

    // test("new datasets should be empty") {
    //     val res = createLigature.instance.use { instance  =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             res <- instance.query(testDataset).use { tx =>
    //                 tx.allStatements().compile.toList
    //             }
    //         } yield res
    //     }.unsafeRunSync()
    //     assert(res.isEmpty)
    // }

    // test("create new entity") {
    //     val (entity1, entity2) = createLigature.instance.use { instance  =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             _ <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     _ <- tx.newAnonymousEntity()
    //                     _ <- tx.newAnonymousEntity()
    //                 } yield ()
    //             }
    //             res <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     entity3 <- tx.newAnonymousEntity()
    //                     entity4 <- tx.newAnonymousEntity()
    //                 } yield (entity3, entity4)
    //             }
    //         } yield res
    //     }.unsafeRunSync()
    //     assertNotEquals(entity1.right.get.name, entity2.right.get.name)
    //     //TODO make sure names are UUIDS + Test also with prefixes
    // }

    // test("adding statements to datasets") {
    //     val res = createLigature.instance.use { instance  =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             _ <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     ps1  <- tx.addStatement(Statement(entity1, a, entity2))
    //                     ps2  <- tx.addStatement(Statement(entity1, a, entity2)) //dupes get added since they'll have unique contexts
    //                     ps3  <- tx.addStatement(Statement(entity1, a, entity3))
    //                 } yield(ps1, ps2, ps3)
    //             }
    //             statements  <- instance.query(testDataset).use { tx =>
    //                 tx.allStatements().compile.toList
    //             }
    //         } yield statements
    //     }.unsafeRunSync().map(_.right.get).map(_.statement).toSet
    //     assertEquals(res, Set(
    //         Statement(entity1, a, entity2),
    //         Statement(entity1, a, entity2),
    //         Statement(entity1, a, entity3)))
    // }

    // test("removing statements from datasets") {
    //     val res = createLigature.instance.use { instance =>
    //         for {
    //             _ <- instance.createDataset(testDataset)
    //             ps2 <- instance.write(testDataset).use { tx =>
    //                 for {
    //                     ps1 <- tx.addStatement(Statement(entity1, a, entity2))
    //                     ps2 <- tx.addStatement(Statement(entity3, a, entity2))
    //                     _ <- tx.removeStatement(ps1.right.get)
    //                     _ <- tx.removeStatement(ps1.right.get)
    //                     //below doesn't actually remove since context is different
    //                     _ <- tx.removeStatement(PersistedStatement(Statement(entity3, a, entity2), entity1))
    //                 } yield ps2
    //             }
    //             statements <- instance.query(testDataset).use { tx =>
    //                 tx.allStatements().compile.toList
    //             }
    //         } yield (ps2, statements)
    //     }.unsafeRunSync()
    //     assertEquals(res._2.toSet, Set(res._1))
    // }

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
