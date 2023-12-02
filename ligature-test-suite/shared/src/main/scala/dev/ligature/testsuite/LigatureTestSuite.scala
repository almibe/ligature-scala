/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature._
import munit.FunSuite

abstract class LigatureTestSuite extends FunSuite {
  def createLigature(): Ligature

  val testGraph = Graph("test/test")
  val testGraph2 = Graph("test/test2")
  val testGraph3 = Graph("test3/test")
  val a = Label("a")
  val b = Label("b")
  val label1 = Label("a")
  val label2 = Label("b")
  val label3 = Label("c")

  val setup = FunFixture[Ligature](
    setup = { test =>
      //Files.createTempFile("tmp", test.name)
      createLigature()
    },
    teardown = { instance =>
      instance.close()
    }
  )

  setup.test("create and close store".only) { instance =>
    assertEquals(instance.allGraphs().toList, List())
  }

  setup.test("creating a new graph".only) { instance =>
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
    instance.createGraph(Graph("a"))
    instance.createGraph(Graph("app"))
    instance.createGraph(Graph("b"))
    instance.createGraph(Graph("be"))
    instance.createGraph(Graph("bee"))
    instance.createGraph(
      Graph("test1/test")
    )
    instance.createGraph(
      Graph("test2/test2")
    )
    instance.createGraph(
      Graph("test3/test")
    )
    instance.createGraph(Graph("test4"))
    instance.createGraph(Graph("z"))
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
    val res = instance.allEdges(testGraph).toList
    assertEquals(res, List())
  }

  setup.test("adding statements to graphs") { instance =>
      instance.createGraph(testGraph)
      instance.addEdges(
          testGraph,
            Seq(
              Edge(label1, a, label2),
              Edge(label1, a, label2), // dupe
              Edge(label1, a, label3)
            ).iterator
        )
        val edges = instance.allEdges(testGraph).toSet
      assertEquals(
        edges,
        Set(
          Edge(label1, a, label2),
          Edge(label1, a, label3)
        )
      )
    }

  setup.test("add Edge with IntegerLiteral Value") { instance =>
    instance.createGraph(testGraph)
    instance.addEdges(
      testGraph,
      Seq(
        Edge(label1, a, label2),
        Edge(label1, a, LigatureLiteral.IntegerLiteral(100)),
        Edge(label1, a, LigatureLiteral.IntegerLiteral(101)),
        Edge(label1, a, LigatureLiteral.IntegerLiteral(100)),
        Edge(label2, a, LigatureLiteral.IntegerLiteral(-243729))
      ).iterator
    )
    val edges = instance.allEdges(testGraph).toSet
    assertEquals(
      edges,
      Set(
        Edge(label1, a, label2),
        Edge(label1, a, LigatureLiteral.IntegerLiteral(100)),
        Edge(label1, a, LigatureLiteral.IntegerLiteral(101)),
        Edge(label2, a, LigatureLiteral.IntegerLiteral(-243729))
      )
    )
  }

  setup.test("add Edge with StringLiteral Value") { instance =>
    instance.createGraph(testGraph)
    instance.addEdges(
      testGraph,
        Seq(
          Edge(label1, a, label2),
          Edge(label1, a, LigatureLiteral.StringLiteral("text")),
          Edge(label1, a, LigatureLiteral.StringLiteral("text2")),
          Edge(label1, a, LigatureLiteral.StringLiteral("text")),
          Edge(label2, a, LigatureLiteral.StringLiteral("text"))
        ).iterator
    )
    val edges = instance.allEdges(testGraph).toSet
    assertEquals(
      edges,
      Set(
        Edge(label1, a, label2),
        Edge(label1, a, LigatureLiteral.StringLiteral("text")),
        Edge(label1, a, LigatureLiteral.StringLiteral("text2")),
        Edge(label2, a, LigatureLiteral.StringLiteral("text"))
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
// //   //         _ <- tx.addEdge(Edge(entity, attribute, value))
// //   //       } yield IO.unit
// //   //     }
// //   //     statements <- instance.query(testGraph) { tx =>
// //   //       tx.allEdges().toList
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
    instance.addEdges(testGraph, Seq(Edge(label1, a, label2)).iterator)
    instance.removeEdges(
      testGraph,
      Seq(Edge(label1, a, label2), Edge(label1, a, label2)).iterator
    )
    val edges = instance.allEdges(testGraph).toSet
    assertEquals(edges, Set())
  }

  setup.test("removing statements from graphs with dupe") { instance =>
    instance.createGraph(testGraph)
    instance.addEdges(
      testGraph,
      Seq(Edge(label1, a, label2), Edge(label3, a, label2), Edge(label1, a, label2)).iterator
    )
    instance.removeEdges(testGraph, Seq(Edge(label1, a, label2)).iterator)
    val edges = instance.allEdges(testGraph).toSet
    assertEquals(edges, Set(Edge(label3, a, label2)))
  }

  setup.test("removing statements from graphs with duplicate Strings") { instance =>
    instance.createGraph(testGraph)
    instance.addEdges(
      testGraph,
        Seq(
          Edge(label1, a, LigatureLiteral.StringLiteral("hello")),
          Edge(label1, a, LigatureLiteral.StringLiteral("hello")),
          Edge(label2, a, LigatureLiteral.StringLiteral("hello"))
        ).iterator
    )
    instance.removeEdges(
      testGraph,
      Seq(Edge(label1, a, LigatureLiteral.StringLiteral("hello"))).iterator
    )
    val edges = instance.allEdges(testGraph).toSet
    assertEquals(edges, Set(Edge(label2, a, LigatureLiteral.StringLiteral("hello"))))
  }

// //   // setup.test("allow canceling WriteTx by throwing exception") {
// //   //     val res = createLigature.instance.use { instance  =>
// //   //         for {
// //   //             _ <- instance.createGraph(testGraph)
// //   //             _ <- instance.write(testGraph).use { tx =>
// //   //                 for {
// //   //                     _ <- tx.addEdge(Edge(label1, a, label2))
// //   //                     _ <- tx.cancel()
// //   //                 } yield ()
// //   //             }
// //   //             _ <- instance.write(testGraph).use { tx =>
// //   //                 for {
// //   //                     _ <- tx.addEdge(Edge(label2, a, label3))
// //   //                     _ <- tx.addEdge(Edge(label3, a, label2))
// //   //                 } yield ()
// //   //             }
// //   //             statements <- instance.query(testGraph).use { tx =>
// //   //                 tx.allEdges().toList
// //   //             }
// //   //         } yield statements
// //   //     }.unsafeRunSync().map(_.right.get).map(_.statement).toSet
// //   //     assertEquals(res, Set(
// //   //         Edge(label2, a, label3),
// //   //         Edge(label3, a, label2)))
// //   // }

  setup.test("matching statements in graphs") { instance =>
    instance.createGraph(testGraph)
    instance.addEdges(
      testGraph,
        Seq(
          Edge(label1, a, LigatureLiteral.StringLiteral("Hello")),
          Edge(label2, a, label1),
          Edge(label2, a, label3),
          Edge(label3, b, label2),
          Edge(label3, b, LigatureLiteral.StringLiteral("Hello"))
        ).iterator
    )
    val (all, as, hellos, helloa) = instance.query(testGraph) { tx =>
      val all = tx.matchEdges().toSet
      val as = tx.matchEdges(None, Some(a)).toSet
      val hellos = tx
          .matchEdges(None, None, Some(LigatureLiteral.StringLiteral("Hello")))
          .toSet
      val helloa = tx
          .matchEdges(None, Some(a), Some(LigatureLiteral.StringLiteral("Hello")))
          .toSet
      (all, as, hellos, helloa)
    }
    assertEquals(
      all,
      Set(
        Edge(label1, a, LigatureLiteral.StringLiteral("Hello")),
        Edge(label2, a, label1),
        Edge(label2, a, label3),
        Edge(label3, b, label2),
        Edge(label3, b, LigatureLiteral.StringLiteral("Hello"))
      )
    )
    assertEquals(
      as,
      Set(
        Edge(label1, a, LigatureLiteral.StringLiteral("Hello")),
        Edge(label2, a, label1),
        Edge(label2, a, label3)
      )
    )
    assertEquals(
      hellos,
      Set(
        Edge(label1, a, LigatureLiteral.StringLiteral("Hello")),
        Edge(label3, b, LigatureLiteral.StringLiteral("Hello"))
      )
    )
    assertEquals(
      helloa,
      Set(
        Edge(label1, a, LigatureLiteral.StringLiteral("Hello"))
      )
    )
  }

// //  setup.test("matching statements with literals and ranges in graphs") {
// //    val instance = createLigature
// //    val res = for {
// //      _ <- instance.createGraph(testGraph)
// //      _ <- instance.write(testGraph) { tx =>
// //        for {
// //          _ <- tx.addEdge(Edge(label1, a, label2))
// //          _ <- tx
// //            .addEdge(Edge(label1, b, StringLiteral("add")))
// //          _ <- tx
// //            .addEdge(Edge(label1, a, IntegerLiteral(5L)))
// //          _ <- tx
// //            .addEdge(Edge(label2, a, IntegerLiteral(3L)))
// //          _ <- tx.addEdge(
// //            Edge(label2, a, StringLiteral("divide"))
// //          )
// //          _ <- tx.addEdge(Edge(label2, b, label3))
// //          _ <- tx
// //            .addEdge(Edge(label3, a, IntegerLiteral(7L)))
// //          _ <- tx.addEdge(
// //            Edge(label3, b, StringLiteral("decimal"))
// //          )
// //        } yield ()
// //      }
// //      res <- instance.query(testGraph) { tx =>
// //        for {
// //          res1 <- tx
// //            .matchEdgesRange(None, None, StringLiteralRange("a", "dd"))
// //            .compile
// //            .toList
// //          res2 <- tx
// //            .matchEdgesRange(None, None, IntegerLiteralRange(3, 6))
// //            .compile
// //            .toList
// //          res3 <- tx
// //            .matchEdgesRange(None, Some(b), StringLiteralRange("ae", "df"))
// //            .compile
// //            .toList
// //        } yield (res1.toSet, res2.toSet, res3.toSet)
// //      }
// //    } yield res
// //    res.map { (res1, res2, res3) =>
// //      assertEquals(
// //        res1,
// //        Set(
// //          Edge(label1, b, StringLiteral("add"))
// //        )
// //      )
// //      assertEquals(
// //        res2,
// //        Set(
// //          Edge(label2, a, IntegerLiteral(3L)),
// //          Edge(label1, a, IntegerLiteral(5L))
// //        )
// //      )
// //      assertEquals(
// //        res3,
// //        Set(
// //          Edge(label3, b, StringLiteral("decimal"))
// //        )
// //      )
// //    }
// //  }
}
