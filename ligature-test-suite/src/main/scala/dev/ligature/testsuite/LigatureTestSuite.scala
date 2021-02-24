/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature._
import munit._
import monix.execution.Scheduler.Implicits.global

abstract class LigatureTestSuite extends FunSuite {
  def createLigature: Ligature

  val testDataset = Dataset.fromString("test/test").get
  val testDataset2 = Dataset.fromString("test/test2").get
  val testDataset3 = Dataset.fromString("test3/test").get

  test("create and close store") {
    val res = createLigature.instance.use { instance: LigatureInstance  =>
      instance.allDatasets().toListL
    }.runSyncUnsafe()
    assert(res.isEmpty)
  }

  test("creating a new dataset") {
    val res = createLigature.instance.use { instance: LigatureInstance =>
      for {
        _ <- instance.createDataset(testDataset)
        res <- instance.allDatasets().toListL
      } yield res
    }.runSyncUnsafe()
    assertEquals(res, List(Right(testDataset)))
  }

  test("check if datasets exist") {
    val res = createLigature.instance.use { instance: LigatureInstance =>
      for {
        _ <- instance.createDataset(testDataset)
        exists1 <- instance.datasetExists(testDataset)
        exists2 <- instance.datasetExists(testDataset2)
      } yield (exists1, exists2)
    }.runSyncUnsafe()
    assert(res._1.right.get)
    assert(!res._2.right.get)
  }

  test("match datasets prefix") {
    val res = createLigature.instance.use { instance: LigatureInstance =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.createDataset(testDataset2)
        _ <- instance.createDataset(testDataset3)
        res1 <- instance.matchDatasetsPrefix("test").toListL
        res2 <- instance.matchDatasetsPrefix("test/").toListL
        res3 <- instance.matchDatasetsPrefix("snoo").toListL
      } yield (res1, res2, res3)
    }.runSyncUnsafe()
    assertEquals(res._1.length, 3)
    assertEquals(res._2.length, 2)
    assertEquals(res._3.length, 0)
  }

  test("match datasets range") {
    val res = createLigature.instance.use { instance: LigatureInstance =>
      for {
        _ <- instance.createDataset(Dataset.fromString("a").get)
        _ <- instance.createDataset(Dataset.fromString("app").get)
        _ <- instance.createDataset(Dataset.fromString("b").get)
        _ <- instance.createDataset(Dataset.fromString("be").get)
        _ <- instance.createDataset(Dataset.fromString("bee").get)
        _ <- instance.createDataset(Dataset.fromString("test1/test").get)
        _ <- instance.createDataset(Dataset.fromString("test2/test2").get)
        _ <- instance.createDataset(Dataset.fromString("test3/test").get)
        _ <- instance.createDataset(Dataset.fromString("test4").get)
        _ <- instance.createDataset(Dataset.fromString("z").get)
        res <- instance.allDatasets().toListL
        res1 <- instance.matchDatasetsRange("a", "b").toListL
        res2 <- instance.matchDatasetsRange("be", "test3").toListL
        res3 <- instance.matchDatasetsRange("snoo", "zz").toListL
      } yield (res1, res2, res3)
    }.runSyncUnsafe()
    assertEquals(res._1.length, 2) //TODO check instances not just counts
    assertEquals(res._2.length, 4) //TODO check instances not just counts
    assertEquals(res._3.length, 5) //TODO check instances not just counts
  }

  test("create and delete new dataset") {
    val res = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.deleteDataset(testDataset)
        _ <- instance.deleteDataset(testDataset2)
        res <- instance.allDatasets().toListL
      } yield res
    }.runSyncUnsafe()
    assert(res.isEmpty)
  }

  test("new datasets should be empty") {
    val res = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        res <- instance.query(testDataset).use { tx =>
          tx.allStatements().toListL
        }
      } yield res
    }.runSyncUnsafe()
    assert(res.isEmpty)
  }

//  test("new node test") {
//    val res = createLigature.instance.use { instance  =>
//      for {
//        _ <- instance.write.use { tx =>
//          for {
//            nn1 <- tx.newNode(testDataset)
//            nn2 <- tx.newNode(testDataset)
//            _   <- tx.addStatement(testDataset, Statement(nn1, a, nn2))
//            nn3 <- tx.newNode(testDataset)
//            nn4 <- tx.newNode(testDataset)
//            _   <- tx.addStatement(testDataset, Statement(nn3, a, nn4))
//          } yield ()
//        }
//        res <- instance.query.use { tx =>
//          tx.allStatements(testDataset).compile.toList
//        }
//      } yield res
//    }.runSyncUnsafe().toSet
//    assertEquals(res.map { _.statement }, Set(
//      Statement(BlankNode(1), a, BlankNode(2)),
//      Statement(BlankNode(4), a, BlankNode(5))))
//  }
//
//  test("adding statements to datasets") {
//    val res = createLigature.instance.use { instance  =>
//      for {
//        _ <- instance.write.use { tx =>
//          for {
//            ent1 <- tx.newNode(testDataset)
//            ent2 <- tx.newNode(testDataset)
//            _    <- tx.addStatement(testDataset, Statement(ent1, a, ent2))
//            _    <- tx.addStatement(testDataset, Statement(ent1, a, ent2))
//          } yield()
//        }
//        res  <- instance.query.use { tx =>
//          tx.allStatements(testDataset).map { _.statement }.compile.toList
//        }
//      } yield res
//    }.runSyncUnsafe().toSet
//    assertEquals(res, Set(Statement(BlankNode(1), a, BlankNode(2)),
//      Statement(BlankNode(1), a, BlankNode(2))))
//  }
//
//  test("removing statements from datasets") {
//    val res = createLigature.instance.use { instance =>
//      for {
//        _ <- instance.write.use { tx =>
//          for {
//            nn1 <- tx.newNode(testDataset)
//            nn2 <- tx.newNode(testDataset)
//            nn3 <- tx.newNode(testDataset)
//            _ <- tx.addStatement(testDataset, Statement(nn1, a, nn2))
//            _ <- tx.addStatement(testDataset, Statement(nn3, a, nn2))
//            _ <- tx.removeStatement(testDataset, Statement(nn1, a, nn2))
//            _ <- tx.removeStatement(testDataset, Statement(nn1, a, nn2))
//            _ <- tx.removeStatement(testDataset, Statement(nn2, a, nn1))
//          } yield ()
//        }
//        res <- instance.query.use { tx =>
//          tx.allStatements(testDataset).map {
//            _.statement
//          }.compile.toList
//        }
//      } yield res
//    }.runSyncUnsafe().toSet
//    assertEquals(res, Set(Statement(BlankNode(3), a, BlankNode(2))))
//  }

  ////  test("matching against a non-existent dataset") {
  ////    val res = createLigature.instance.use { instance  =>
  ////    val (r1, r2) = instance.query.use { tx =>
  ////      for {
  ////        r1 <- tx.matchStatements(testDataset, null, null, StringLiteral("French")).compile.toList
  ////        r2 <- tx.matchStatements(testDataset, null, a, null).compile.toList
  ////      } yield(r1, r2)
  ////    }
  ////  }
  ////
  ////  test("matching statements in datasets") {
  ////    val res = createLigature.instance.use { instance  =>
  ////    lateinit var valjean: Node
  ////    lateinit var javert: Node
  ////    instance.write.use { tx =>
  ////      valjean = tx.newNode(testDataset)
  ////      javert = tx.newNode(testDataset)
  ////      tx.addStatement(testDataset, Statement(valjean, Predicate("nationality"), StringLiteral("French")))
  ////      tx.addStatement(testDataset, Statement(valjean, Predicate("prisonNumber"), LongLiteral(24601)))
  ////      tx.addStatement(testDataset, Statement(javert, Predicate("nationality"), StringLiteral("French")))
  ////    }
  ////    instance.query.use { tx =>
  ////      tx.matchStatements(testDataset, null, null, StringLiteral("French"))
  ////              .toSet() shouldBe setOf(
  ////                  Statement(valjean, Predicate("nationality"), StringLiteral("French")),
  ////                  Statement(javert, Predicate("nationality"), StringLiteral("French"))
  ////      )
  ////      tx.matchStatements(testDataset, null, null, LongLiteral(24601))
  ////              .toSet() shouldBe setOf(
  ////                  Statement(valjean, Predicate("prisonNumber"), LongLiteral(24601))
  ////      )
  ////      tx.matchStatements(testDataset, valjean)
  ////              .toSet() shouldBe setOf(
  ////                  Statement(valjean, Predicate("nationality"), StringLiteral("French")),
  ////                  Statement(valjean, Predicate("prisonNumber"), LongLiteral(24601))
  ////      )
  ////      tx.matchStatements(testDataset, javert, Predicate("nationality"), StringLiteral("French"))
  ////              .toSet() shouldBe setOf(
  ////                  Statement(javert, Predicate("nationality"), StringLiteral("French"))
  ////      )
  ////      tx.matchStatements(testDataset, null, null, null)
  ////              .toSet() shouldBe setOf(
  ////                  Statement(valjean, Predicate("nationality"), StringLiteral("French")),
  ////                  Statement(valjean, Predicate("prisonNumber"), LongLiteral(24601)),
  ////                  Statement(javert, Predicate("nationality"), StringLiteral("French"))
  ////      )
  ////    }
  ////  }
  ////
  ////  test("matching statements with literals and ranges in datasets") {
  ////    val res = createLigature.instance.use { instance  =>
  ////    lateinit var valjean: Node
  ////    lateinit var javert: Node
  ////    lateinit var trout: Node
  ////    instance.write.use { tx =>
  ////      valjean = tx.newNode(testDataset)
  ////      javert = tx.newNode(testDataset)
  ////      trout = tx.newNode(testDataset)
  ////      tx.addStatement(testDataset, Statement(valjean, Predicate("nationality"), StringLiteral("French")))
  ////      tx.addStatement(testDataset, Statement(valjean, Predicate("prisonNumber"), LongLiteral(24601)))
  ////      tx.addStatement(testDataset, Statement(javert, Predicate("nationality"), StringLiteral("French")))
  ////      tx.addStatement(testDataset, Statement(javert, Predicate("prisonNumber"), LongLiteral(24602)))
  ////      tx.addStatement(testDataset, Statement(trout, Predicate("nationality"), StringLiteral("American")))
  ////      tx.addStatement(testDataset, Statement(trout, Predicate("prisonNumber"), LongLiteral(24603)))
  ////    }
  ////    instance.query.use { tx =>
  ////      tx.matchStatements(testDataset, null, null, StringLiteralRange("French", "German"))
  ////              .toSet() shouldBe setOf(
  ////                  Statement(valjean, Predicate("nationality"), StringLiteral("French")),
  ////                  Statement(javert, Predicate("nationality"), StringLiteral("French"))
  ////      )
  ////      tx.matchStatements(testDataset, null, null, LongLiteralRange(24601, 24603))
  ////              .toSet() shouldBe setOf(
  ////                  Statement(valjean, Predicate("prisonNumber"), LongLiteral(24601)),
  ////                  Statement(javert, Predicate("prisonNumber"), LongLiteral(24602))
  ////      )
  ////      tx.matchStatements(testDataset, valjean, null, LongLiteralRange(24601, 24603))
  ////              .toSet() shouldBe setOf(
  ////                  Statement(valjean, Predicate("prisonNumber"), LongLiteral(24601))
  ////      )
  ////    }
  ////  }
  ////
  ////  test("matching statements with dataset literals in datasets") {
  ////    val res = createLigature.instance.use { instance  =>
  ////    val dataset = store.createDataset(NamedNode("test"))
  ////    dataset shouldNotBe null
  ////    val tx = dataset.writeTx()
  ////    TODO("Add values")
  ////    tx.commit()
  ////    val tx = dataset.tx()
  ////    TODO("Add assertions")
  ////    tx.cancel() // TODO add test running against a non-existant dataset w/ match-statement calls
  ////  }
}