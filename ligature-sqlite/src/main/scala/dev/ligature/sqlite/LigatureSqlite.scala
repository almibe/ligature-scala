/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.{Ligature, QueryTx}


import scala.jdk.CollectionConverters.*
import java.nio.file.Path
import dev.ligature.Statement
import dev.ligature.LigatureValue
import dev.ligature.DatasetName
import scala.math.Ordered.orderingToOrdered
import java.sql.DriverManager
import java.sql.Connection

def createLigatureSqlite(path: Path): Ligature =
  val connection = DriverManager.getConnection("jdbc:sqlite:test.db");
  val statement = connection.createStatement();
  statement.setQueryTimeout(30);  // set timeout to 30 sec.
  statement.executeUpdate("drop table if exists person");
  statement.executeUpdate("create table person (id integer, name string)");
  statement.executeUpdate("insert into person values(1, 'leo')");
  statement.executeUpdate("insert into person values(2, 'yui')");
  val rs = statement.executeQuery("select * from person");
  println("name = " + rs.getString("name"));
  println("id = " + rs.getInt("id"));
  val ligatureInstance = LigatureSqlite(connection)
  ligatureInstance

private final class LigatureSqlite(connection: Connection) extends Ligature {
  override def allDatasets(): Iterator[DatasetName] = ???

  override def createDataset(graph: DatasetName): Unit = ???

  override def deleteDataset(graph: DatasetName): Unit = ???

  override def matchDatasetsPrefix(prefix: String): Iterator[DatasetName] = ???

  override def graphExists(graph: DatasetName): Boolean = ???

  override def matchDatasetsRange(start: String, end: String): Iterator[DatasetName] = ???

  override def allStatements(graph: DatasetName): Iterator[Statement] = ???

  override def addStatements(graph: DatasetName, statements: Iterator[Statement]): Unit = ???

  override def removeStatements(graph: DatasetName, edges: Iterator[Statement]): Unit = ???

  override def query[T](graph: DatasetName)(fn: QueryTx => T): T = ???

  override def close(): Unit = ???
}

def targetValue(value: LigatureValue): Any =
  value match
    case LigatureValue.IntegerValue(value) => value
    case LigatureValue.StringValue(value)  => value
    case LigatureValue.Identifier(value)   => value
    case LigatureValue.BytesValue(value)   => value
    case LigatureValue.Record(value)       => value

def valueToPersist(value: LigatureValue): Comparable[?] =
  value match
    case LigatureValue.IntegerValue(value) => value
    case LigatureValue.StringValue(value)  => value
    case LigatureValue.Identifier(value)   => value
    case value: LigatureValue.BytesValue   => encodeLigatureValue(value)
    case value: LigatureValue.Record       => encodeLigatureValue(value)

enum TypeCode(val code: Int):
  case IdentifierType extends TypeCode(0)
  case IntType extends TypeCode(1)
  case StringType extends TypeCode(2)
  case BytesType extends TypeCode(3)
  case RecordType extends TypeCode(4)

def intToTypeCode(code: Int): TypeCode =
  code match
    case 0 => TypeCode.IdentifierType
    case 1 => TypeCode.IntType
    case 2 => TypeCode.StringType
    case 3 => TypeCode.BytesType
    case 4 => TypeCode.RecordType
    case _ => ???

def valueType(value: LigatureValue): Int =
  value match
    case LigatureValue.IntegerValue(_) => TypeCode.IntType.code
    case LigatureValue.StringValue(_)  => TypeCode.StringType.code
    case LigatureValue.Identifier(_)   => TypeCode.IdentifierType.code
    case LigatureValue.BytesValue(_)   => TypeCode.BytesType.code
    case LigatureValue.Record(_)       => TypeCode.RecordType.code
