/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature._
import munit.FunSuite

abstract class LigatureTestSuite extends FunSuite {
  def createLigature(): Ligature

  val testGraph = DatasetName("test/test")
  val testGraph2 = DatasetName("test/test2")
  val testGraph3 = DatasetName("test3/test")
  val a: LigatureValue.Identifier = LigatureValue.Identifier("a")
  val b: LigatureValue.Identifier = LigatureValue.Identifier("b")
  val label1: LigatureValue.Identifier = LigatureValue.Identifier("a")
  val label2: LigatureValue.Identifier = LigatureValue.Identifier("b")
  val label3: LigatureValue.Identifier = LigatureValue.Identifier("c")

  val setup = FunFixture[Ligature](
    setup = { test =>
      // Files.createTempFile("tmp", test.name)
      createLigature()
    },
    teardown = { instance =>
      instance.close()
    }
  )

  setup.test("create and close store") { instance =>
    assertEquals(instance.allGraphs().toList, List())
  }

  setup.test("creating a new graph") { instance =>
    instance.createGraph(testGraph)
    val res = instance.allGraphs().toList
    assertEquals(res, List(testGraph))
  }

  setup.test("check if graphs exist") { instance =>
    instance.createGraph(testGraph)
    val exists1 = instance.graphExists(testGraph)
    val exists2 = instance.graphExists(testGraph2)
    val res = (exists1, exists2)
    assertEquals(res, (true, false))
  }

  setup.test("match graphs prefix exact") { instance =>
    instance.createGraph(testGraph)
    val res = instance.matchGraphsPrefix("test/test").toList
    assertEquals(res.length, 1)
  }

  setup.test("match graphs prefix") { instance =>
    instance.createGraph(testGraph)
    instance.createGraph(testGraph2)
    instance.createGraph(testGraph3)
    val res1 = instance.matchGraphsPrefix("test").length
    val res2 = instance.matchGraphsPrefix("test/").length
    val res3 = instance.matchGraphsPrefix("snoo").length
    assertEquals((res1, res2, res3), (3, 2, 0))
  }

  setup.test("match graphs range") { instance =>
    instance.createGraph(DatasetName("a"))
    instance.createGraph(DatasetName("app"))
    instance.createGraph(DatasetName("b"))
    instance.createGraph(DatasetName("be"))
    instance.createGraph(DatasetName("bee"))
    instance.createGraph(
      DatasetName("test1/test")
    )
    instance.createGraph(
      DatasetName("test2/test2")
    )
    instance.createGraph(
      DatasetName("test3/test")
    )
    instance.createGraph(DatasetName("test4"))
    instance.createGraph(DatasetName("z"))
    instance.allGraphs().toList.length
    val res1 = instance.matchGraphsRange("a", "b").toList.length
    val res2 = instance.matchGraphsRange("be", "test3").toList.length
    val res3 = instance.matchGraphsRange("snoo", "zz").toList.length
    assertEquals((res1, res2, res3), (2, 4, 5)) // TODO check instances not just counts
  }

  setup.test("create and delete new graph") { instance =>
    instance.createGraph(testGraph)
    instance.deleteGraph(testGraph)
    instance.deleteGraph(testGraph2)
    val res = instance.allGraphs().toList
    assertEquals(res, List())
  }

  setup.test("new graphs should be empty") { instance =>
    instance.createGraph(testGraph)
    val res = instance.allStatements(testGraph).toList
    assertEquals(res, List())
  }

  setup.test("adding statements to graphs") { instance =>
    instance.createGraph(testGraph)
    instance.addStatements(
      testGraph,
      Seq(
        Statement(label1, a, label2),
        Statement(label1, a, label2), // dupe
        Statement(label1, a, label3)
      ).iterator
    )
    val edges = instance.allStatements(testGraph).toSet
    assertEquals(
      edges,
      Set(
        Statement(label1, a, label2),
        Statement(label1, a, label3)
      )
    )
  }

  setup.test("add Statement with IntegerValue Value") { instance =>
    instance.createGraph(testGraph)
    instance.addStatements(
      testGraph,
      Seq(
        Statement(label1, a, label2),
        Statement(label1, a, LigatureValue.IntegerValue(100)),
        Statement(label1, a, LigatureValue.IntegerValue(101)),
        Statement(label1, a, LigatureValue.IntegerValue(100)),
        Statement(label2, a, LigatureValue.IntegerValue(-243729))
      ).iterator
    )
    val edges = instance.allStatements(testGraph).toSet
    assertEquals(
      edges,
      Set(
        Statement(label1, a, label2),
        Statement(label1, a, LigatureValue.IntegerValue(100)),
        Statement(label1, a, LigatureValue.IntegerValue(101)),
        Statement(label2, a, LigatureValue.IntegerValue(-243729))
      )
    )
  }

  setup.test("add Statement with StringValue Value") { instance =>
    instance.createGraph(testGraph)
    instance.addStatements(
      testGraph,
      Seq(
        Statement(label1, a, label2),
        Statement(label1, a, LigatureValue.StringValue("text")),
        Statement(label1, a, LigatureValue.StringValue("text2")),
        Statement(label1, a, LigatureValue.StringValue("text")),
        Statement(label2, a, LigatureValue.StringValue("text"))
      ).iterator
    )
    val edges = instance.allStatements(testGraph).toSet
    assertEquals(
      edges,
      Set(
        Statement(label1, a, label2),
        Statement(label1, a, LigatureValue.StringValue("text")),
        Statement(label1, a, LigatureValue.StringValue("text2")),
        Statement(label2, a, LigatureValue.StringValue("text"))
      )
    )
  }

// //   // setup.test("new identifiers") {
// //   //   val instance = createLigature
// //   //   val res = for {
// //   //     _ <- instance.createGraph(testGraph)
// //   //     _ <- instance.write(testGraph) { tx =>
// //   //       for {
// //   //         entity <- tx.newLabel("entity-")
// //   //         attribute <- tx.newLabel("attribute-")
// //   //         value <- tx.newLabel("value-")
// //   //         _ <- tx.addStatement(Statement(entity, attribute, value))
// //   //       } yield IO.unit
// //   //     }
// //   //     statements <- instance.query(testGraph) { tx =>
// //   //       tx.allStatements().toList
// //   //     }
// //   //   } yield statements.head
// //   //   res.map { it =>
// //   //     assert(it.entity.name.startsWith("entity-"))
// //   //     assert(it.attribute.name.startsWith("attribute-"))
// //   //     it.value match {
// //   //       case Label(id) => assert(id.startsWith("value-"))
// //   //       case _              => assert(false)
// //   //     }
// //   //   }
// //   // }

  setup.test("removing statements from graphs") { instance =>
    instance.createGraph(testGraph)
    instance.addStatements(testGraph, Seq(Statement(label1, a, label2)).iterator)
    instance.removeStatements(
      testGraph,
      Seq(Statement(label1, a, label2), Statement(label1, a, label2)).iterator
    )
    val edges = instance.allStatements(testGraph).toSet
    assertEquals(edges, Set())
  }

  setup.test("removing statements from graphs with dupe") { instance =>
    instance.createGraph(testGraph)
    instance.addStatements(
      testGraph,
      Seq(
        Statement(label1, a, label2),
        Statement(label3, a, label2),
        Statement(label1, a, label2)
      ).iterator
    )
    instance.removeStatements(testGraph, Seq(Statement(label1, a, label2)).iterator)
    val edges = instance.allStatements(testGraph).toSet
    assertEquals(edges, Set(Statement(label3, a, label2)))
  }

  setup.test("removing statements from graphs with duplicate Strings") { instance =>
    instance.createGraph(testGraph)
    instance.addStatements(
      testGraph,
      Seq(
        Statement(label1, a, LigatureValue.StringValue("hello")),
        Statement(label1, a, LigatureValue.StringValue("hello")),
        Statement(label2, a, LigatureValue.StringValue("hello"))
      ).iterator
    )
    instance.removeStatements(
      testGraph,
      Seq(Statement(label1, a, LigatureValue.StringValue("hello"))).iterator
    )
    val edges = instance.allStatements(testGraph).toSet
    assertEquals(edges, Set(Statement(label2, a, LigatureValue.StringValue("hello"))))
  }

// //   // setup.test("allow canceling WriteTx by throwing exception") {
// //   //     val res = createLigature.instance.use { instance  =>
// //   //         for {
// //   //             _ <- instance.createGraph(testGraph)
// //   //             _ <- instance.write(testGraph).use { tx =>
// //   //                 for {
// //   //                     _ <- tx.addStatement(Statement(label1, a, label2))
// //   //                     _ <- tx.cancel()
// //   //                 } yield ()
// //   //             }
// //   //             _ <- instance.write(testGraph).use { tx =>
// //   //                 for {
// //   //                     _ <- tx.addStatement(Statement(label2, a, label3))
// //   //                     _ <- tx.addStatement(Statement(label3, a, label2))
// //   //                 } yield ()
// //   //             }
// //   //             statements <- instance.query(testGraph).use { tx =>
// //   //                 tx.allStatements().toList
// //   //             }
// //   //         } yield statements
// //   //     }.unsafeRunSync().map(_.right.get).map(_.statement).toSet
// //   //     assertEquals(res, Set(
// //   //         Statement(label2, a, label3),
// //   //         Statement(label3, a, label2)))
// //   // }

  setup.test("matching statements in graphs") { instance =>
    instance.createGraph(testGraph)
    instance.addStatements(
      testGraph,
      Seq(
        Statement(label1, a, LigatureValue.StringValue("Hello")),
        Statement(label2, a, label1),
        Statement(label2, a, label3),
        Statement(label3, b, label2),
        Statement(label3, b, LigatureValue.StringValue("Hello"))
      ).iterator
    )
    val (all, as, hellos, helloa) = instance.query(testGraph) { tx =>
      val all = tx.matchStatements().toSet
      val as = tx.matchStatements(None, Some(a)).toSet
      val hellos = tx
        .matchStatements(None, None, Some(LigatureValue.StringValue("Hello")))
        .toSet
      val helloa = tx
        .matchStatements(None, Some(a), Some(LigatureValue.StringValue("Hello")))
        .toSet
      (all, as, hellos, helloa)
    }
    assertEquals(
      all,
      Set(
        Statement(label1, a, LigatureValue.StringValue("Hello")),
        Statement(label2, a, label1),
        Statement(label2, a, label3),
        Statement(label3, b, label2),
        Statement(label3, b, LigatureValue.StringValue("Hello"))
      )
    )
    assertEquals(
      as,
      Set(
        Statement(label1, a, LigatureValue.StringValue("Hello")),
        Statement(label2, a, label1),
        Statement(label2, a, label3)
      )
    )
    assertEquals(
      hellos,
      Set(
        Statement(label1, a, LigatureValue.StringValue("Hello")),
        Statement(label3, b, LigatureValue.StringValue("Hello"))
      )
    )
    assertEquals(
      helloa,
      Set(
        Statement(label1, a, LigatureValue.StringValue("Hello"))
      )
    )
  }

// //  setup.test("matching statements with literals and ranges in graphs") {
// //    val instance = createLigature
// //    val res = for {
// //      _ <- instance.createGraph(testGraph)
// //      _ <- instance.write(testGraph) { tx =>
// //        for {
// //          _ <- tx.addStatement(Statement(label1, a, label2))
// //          _ <- tx
// //            .addStatement(Statement(label1, b, StringValue("add")))
// //          _ <- tx
// //            .addStatement(Statement(label1, a, IntegerValue(5L)))
// //          _ <- tx
// //            .addStatement(Statement(label2, a, IntegerValue(3L)))
// //          _ <- tx.addStatement(
// //            Statement(label2, a, StringValue("divide"))
// //          )
// //          _ <- tx.addStatement(Statement(label2, b, label3))
// //          _ <- tx
// //            .addStatement(Statement(label3, a, IntegerValue(7L)))
// //          _ <- tx.addStatement(
// //            Statement(label3, b, StringValue("decimal"))
// //          )
// //        } yield ()
// //      }
// //      res <- instance.query(testGraph) { tx =>
// //        for {
// //          res1 <- tx
// //            .matchStatementsRange(None, None, StringValueRange("a", "dd"))
// //            .compile
// //            .toList
// //          res2 <- tx
// //            .matchStatementsRange(None, None, IntegerValueRange(3, 6))
// //            .compile
// //            .toList
// //          res3 <- tx
// //            .matchStatementsRange(None, Some(b), StringValueRange("ae", "df"))
// //            .compile
// //            .toList
// //        } yield (res1.toSet, res2.toSet, res3.toSet)
// //      }
// //    } yield res
// //    res.map { (res1, res2, res3) =>
// //      assertEquals(
// //        res1,
// //        Set(
// //          Statement(label1, b, StringValue("add"))
// //        )
// //      )
// //      assertEquals(
// //        res2,
// //        Set(
// //          Statement(label2, a, IntegerValue(3L)),
// //          Statement(label1, a, IntegerValue(5L))
// //        )
// //      )
// //      assertEquals(
// //        res3,
// //        Set(
// //          Statement(label3, b, StringValue("decimal"))
// //        )
// //      )
// //    }
// //  }
}
