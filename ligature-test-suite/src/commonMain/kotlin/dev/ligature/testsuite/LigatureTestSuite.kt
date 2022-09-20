/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import arrow.core.none
import arrow.core.Some

abstract class LigatureTestSuite: FunSpec() {
  abstract fun createLigature(): Ligature

  val testDataset = Dataset("test/test")
  val testDataset2 = Dataset("test/test2")
  val testDataset3 = Dataset("test3/test")
  val a = Identifier("a")
  val b = Identifier("b")
  val entity1 = Identifier("a")
  val entity2 = Identifier("b")
  val entity3 = Identifier("c")

  fun runTest(fn: suspend (Ligature) -> Unit) {
    val ligature = createLigature()
    runBlocking {
      fn(ligature)
      ligature.close()
    }
  }

  init {
    test("create and close store") {
      runTest { ligature ->
        ligature.allDatasets().toList() shouldBe listOf()
      }
    }

    test("creating a new dataset") {
      runTest { ligature ->
        ligature.createDataset(testDataset)
        ligature.allDatasets().toList() shouldBe listOf(testDataset)
      }
    }

    test("check if datasets exist") {
      runTest { ligature ->
        ligature.createDataset(testDataset)
        ligature.datasetExists(testDataset) shouldBe true
        ligature.datasetExists(testDataset2) shouldBe false
      }
    }

    test("match datasets prefix exact") {
      runTest {  ligature ->
        ligature.createDataset(testDataset)
        ligature.matchDatasetsPrefix("test/test").toList().size shouldBe 1
      }
    }

    test("match datasets prefix") {
      runTest { ligature ->
        ligature.createDataset(testDataset)
        ligature.createDataset(testDataset2)
        ligature.createDataset(testDataset3)
        ligature.matchDatasetsPrefix("test").toList().size shouldBe 3
        ligature.matchDatasetsPrefix("test/").toList().size shouldBe 2
        ligature.matchDatasetsPrefix("snoo").toList().size shouldBe 0
      }
    }

  test("match datasets range") {
    runTest { ligature ->
      ligature.createDataset(Dataset("a"))
      ligature.createDataset(Dataset("app"))
      ligature.createDataset(Dataset("b"))
      ligature.createDataset(Dataset("be"))
      ligature.createDataset(Dataset("bee"))
      ligature.createDataset(Dataset("test1/test"))
      ligature.createDataset(Dataset("test2/test2"))
      ligature.createDataset(Dataset("test3/test"))
      ligature.createDataset(Dataset("test4"))
      ligature.createDataset(Dataset("z"))
      ligature.matchDatasetsRange("a", "b").toList().size shouldBe 2
      ligature.matchDatasetsRange("be", "test3").toList().size shouldBe 4
      ligature.matchDatasetsRange("snoo", "zz").toList().size shouldBe 5
    }
  }

  test("create and delete new dataset") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.deleteDataset(testDataset)
      ligature.deleteDataset(testDataset2)
      ligature.allDatasets().toList() shouldBe listOf()
    }
  }

  test("new datasets should be empty") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.query(testDataset) { tx -> tx.allStatements().toList() } shouldBe listOf()
    }
  }

  test("adding statements to datasets") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.write(testDataset) { tx ->
        tx.addStatement(Statement(entity1, a, entity2))
        tx.addStatement(Statement(entity1, a, entity2)) // dupe
        tx.addStatement(Statement(entity1, a, entity3))
      }
      ligature.query(testDataset) { tx ->
        tx.allStatements().toSet()
      } shouldBe setOf(
        Statement(entity1, a, entity2),
        Statement(entity1, a, entity3)
      )
    }
  }

  test("add Statement with IntegerLiteral Value") {
    runTest {  ligature ->
        ligature.createDataset(testDataset)
        ligature.write(testDataset) { tx ->
          tx.addStatement(Statement(entity1, a, entity2))
          tx.addStatement(Statement(entity1, a, IntegerLiteral(100)))
          tx.addStatement(Statement(entity1, a, IntegerLiteral(101)))
          tx.addStatement(Statement(entity1, a, IntegerLiteral(100)))
          tx.addStatement(Statement(entity2, a, IntegerLiteral(-243729)))
        }
        ligature.query(testDataset) { tx ->
          tx.allStatements().toSet()
        } shouldBe setOf(
          Statement(entity1, a, entity2),
          Statement(entity1, a, IntegerLiteral(100)),
          Statement(entity1, a, IntegerLiteral(101)),
          Statement(entity2, a, IntegerLiteral(-243729))
        )
    }
  }

  test("add Statement with StringLiteral Value") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.write(testDataset) { tx ->
        tx.addStatement(Statement(entity1, a, entity2))
        tx.addStatement(Statement(entity1, a, StringLiteral("text")))
        tx.addStatement(Statement(entity1, a, StringLiteral("text2")))
        tx.addStatement(Statement(entity1, a, StringLiteral("text")))
        tx.addStatement(Statement(entity2, a, StringLiteral("text")))
      }
      ligature.query(testDataset) { tx ->
        tx.allStatements().toSet()
      } shouldBe setOf(
        Statement(entity1, a, entity2),
        Statement(entity1, a, StringLiteral("text")),
        Statement(entity1, a, StringLiteral("text2")),
        Statement(entity2, a, StringLiteral("text"))
      )
    }
  }

  test("new identifiers") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.write(testDataset) { tx ->
        val entity = tx.newIdentifier("entity-")
        val attribute = tx.newIdentifier("attribute-")
        val value = tx.newIdentifier("value-")
        tx.addStatement(Statement(entity, attribute, value))
      }
      val statement = ligature.query(testDataset) { tx ->
        tx.allStatements().toList().first()
      }
      statement.entity.name.shouldStartWith("entity-")
      statement.attribute.name.shouldStartWith("attribute-")
      when(val value = statement.value) {
        is Identifier -> value.name.shouldStartWith("value-")
        else          -> throw Exception("Failed test.")
      }
    }
  }

  test("removing statements from datasets") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.write(testDataset) { tx ->
        tx.addStatement(Statement(entity1, a, entity2))
        tx.removeStatement(Statement(entity1, a, entity2))
        tx.removeStatement(Statement(entity1, a, entity2))
      }
      ligature.query(testDataset) { tx ->
        tx.allStatements().toList() shouldBe listOf()
      }
    }
  }

  test("removing statements from datasets with dupe") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.write(testDataset) { tx ->
        tx.addStatement(Statement(entity1, a, entity2))
        tx.addStatement(Statement(entity3, a, entity2))
        tx.removeStatement(Statement(entity1, a, entity2))
        tx.removeStatement(Statement(entity1, a, entity2))
      }
      ligature.query(testDataset) { tx ->
        tx.allStatements().toList()
      } shouldBe listOf(Statement(entity3, a, entity2))
    }
  }

  test("removing statements from datasets with duplicate Strings") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.write(testDataset) { tx ->
        tx.addStatement(Statement(entity1, a, StringLiteral("hello")))
        tx.addStatement(Statement(entity1, a, StringLiteral("hello")))
        tx.addStatement(Statement(entity2, a, StringLiteral("hello")))
        tx.removeStatement(Statement(entity1, a, StringLiteral("hello")))
      }
      ligature.query(testDataset) { tx ->
        tx.allStatements().toList()
      } shouldBe listOf(Statement(entity2, a, StringLiteral("hello")))
    }
  }

//  // test("allow canceling WriteTx by throwing exception") {
//  //     val res = createLigature.instance.use { instance  ->
//  //         for {
//  //             ligature.createDataset(testDataset)
//  //             ligature.write(testDataset).use { tx ->
//  //                 for {
//  //                     tx.addStatement(Statement(entity1, a, entity2))
//  //                     tx.cancel()
//  //                 } yield ()
//  //             }
//  //             ligature.write(testDataset).use { tx ->
//  //                 for {
//  //                     tx.addStatement(Statement(entity2, a, entity3))
//  //                     tx.addStatement(Statement(entity3, a, entity2))
//  //                 } yield ()
//  //             }
//  //             statements <- instance.query(testDataset).use { tx ->
//  //                 tx.allStatements().toList()
//  //             }
//  //         } yield statements
//  //     }.unsafeRunSync().map(_.right.get).map(_.statement).toSet
//  //     assertEquals(res, Set(
//  //         Statement(entity2, a, entity3),
//  //         Statement(entity3, a, entity2)))
//  // }

  test("matching statements in datasets") {
    runTest { ligature ->
      ligature.createDataset(testDataset)
      ligature.write(testDataset) { tx ->
        tx.addStatement(
          Statement(entity1, a, StringLiteral("Hello"))
        )
        tx.addStatement(Statement(entity2, a, entity1))
        tx.addStatement(Statement(entity2, a, entity3))
        tx.addStatement(Statement(entity3, b, entity2))
        tx.addStatement(
          Statement(entity3, b, StringLiteral("Hello"))
        )
      }
      ligature.query(testDataset) { tx ->
          tx.matchStatements().toSet() shouldBe setOf(
            Statement(entity1, a, StringLiteral("Hello")),
            Statement(entity2, a, entity1),
            Statement(entity2, a, entity3),
            Statement(entity3, b, entity2),
            Statement(entity3, b, StringLiteral("Hello"))
          )

          tx.matchStatements(null, a).toSet() shouldBe setOf(
            Statement(entity1, a, StringLiteral("Hello")),
            Statement(entity2, a, entity1),
            Statement(entity2, a, entity3)
          )

          tx.matchStatements(null, null, StringLiteral("Hello"))
              .toSet() shouldBe setOf(
                Statement(entity1, a, StringLiteral("Hello")),
                Statement(entity3, b, StringLiteral("Hello"))
              )

          tx.matchStatements(null, a, StringLiteral("Hello"))
            .toSet() shouldBe setOf(
              Statement(entity1, a, StringLiteral("Hello"))
            )
        }
      }
    }
////  test("matching statements with literals and ranges in datasets") {
////    val instance = createLigature
////    val res = for {
////      ligature.createDataset(testDataset)
////      ligature.write(testDataset) { tx ->
////        for {
////          tx.addStatement(Statement(entity1, a, entity2))
////          tx
////            .addStatement(Statement(entity1, b, StringLiteral("add")))
////          tx
////            .addStatement(Statement(entity1, a, IntegerLiteral(5L)))
////          tx
////            .addStatement(Statement(entity2, a, IntegerLiteral(3L)))
////          tx.addStatement(
////            Statement(entity2, a, StringLiteral("divide"))
////          )
////          tx.addStatement(Statement(entity2, b, entity3))
////          tx
////            .addStatement(Statement(entity3, a, IntegerLiteral(7L)))
////          tx.addStatement(
////            Statement(entity3, b, StringLiteral("decimal"))
////          )
////        } yield ()
////      }
////      res <- instance.query(testDataset) { tx ->
////        for {
////          res1 <- tx
////            .matchStatementsRange(none(), none(), StringLiteralRange("a", "dd"))
////
////            .toList()
////          res2 <- tx
////            .matchStatementsRange(none(), none(), IntegerLiteralRange(3, 6))
////
////            .toList()
////          res3 <- tx
////            .matchStatementsRange(none(), Some(b), StringLiteralRange("ae", "df"))
////
////            .toList()
////        } yield (res1.toSet, res2.toSet, res3.toSet)
////      }
////    } yield res
////    res.map { (res1, res2, res3) ->
////      assertEquals(
////        res1,
////        Set(
////          Statement(entity1, b, StringLiteral("add"))
////        )
////      )
////      assertEquals(
////        res2,
////        Set(
////          Statement(entity2, a, IntegerLiteral(3L)),
////          Statement(entity1, a, IntegerLiteral(5L))
////        )
////      )
////      assertEquals(
////        res3,
////        Set(
////          Statement(entity3, b, StringLiteral("decimal"))
////        )
////      )
////    }
////  }
  }
}
