/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import scala.collection.mutable.ListBuffer
import scala.util.boundary, boundary.break

enum Expression:
  case NameExpression(value: Name)
  case IdentifierValue(value: Identifier)
  case IntegerValue(value: Long)
  case StringValue(value: String)
  case BooleanValue(value: Boolean)
  case Nothing
  case Array(value: Seq[Expression])
  case Set(value: Seq[Expression])
  case LetExpression(name: Name, value: Expression)
  case Application(name: Name, arguments: Seq[Expression])
  case Lambda(parameters: Seq[Name], body: Expression)
  case WhenExpression(conditionals: Seq[(Expression, Expression)])
  case Grouping(expressions: Seq[Expression])
  case Triple(entity: Expression, attribute: Expression, value: Expression)
  case Quad(entity: Expression, attribute: Expression, value: Expression, graph: Expression)
  case QuestionMark

def eval(expression: Expression, bindings: Bindings): Either[WanderError, (WanderValue, Bindings)] =
  expression match {
    case Expression.Nothing                => Right((WanderValue.Nothing, bindings))
    case Expression.BooleanValue(value)    => Right((WanderValue.BooleanValue(value), bindings))
    case Expression.IntegerValue(value)    => Right((WanderValue.IntValue(value), bindings))
    case Expression.StringValue(value)     => Right((WanderValue.StringValue(value), bindings))
    case Expression.IdentifierValue(value) => Right((WanderValue.Identifier(value), bindings))
    case Expression.Array(value)           => handleArray(value, bindings)
    case Expression.Set(value)             => handleSet(value, bindings)
    case Expression.Application(name, arguments) => handleApplication(name, arguments, bindings)
    case Expression.NameExpression(name)         => bindings.read(name).map((_, bindings))
    case Expression.LetExpression(name, value)   => handleLetExpression(name, value, bindings)
    case lambda: Expression.Lambda               => Right((WanderValue.Lambda(lambda), bindings))
    case Expression.WhenExpression(conditionals) => handleWhenExpression(conditionals, bindings)
    case Expression.Grouping(expressions)        => handleGrouping(expressions, bindings)
    case Expression.Triple(entity, attribute, value) => handleTriple(entity, attribute, value, bindings)
    case Expression.Quad(entity, attribute, value, graph) => handleQuad(entity, attribute, value, graph, bindings)
    case Expression.QuestionMark => Right((WanderValue.QuestionMark, bindings))
  }

def handleQuery(entity: WanderValue, attribute: WanderValue, value: WanderValue, graphName: String, bindings: Bindings): Either[WanderError, (WanderValue, Bindings)] = {
  val e = entity match {
    case WanderValue.QuestionMark => None
    case WanderValue.Identifier(value) => Some(value)
    case _ => ???
  }
  val a = attribute match {
    case WanderValue.QuestionMark => None
    case WanderValue.Identifier(value) => Some(value)
    case _ => ???
  }
  val v = value match {
    case WanderValue.QuestionMark => None
    case value => Some(value)
  }
  if (bindings.graphs.contains(graphName)) {
    val graph = bindings.graphs(graphName)
    val filtered = graph.filter {statement =>
      val ee = e match {
        case None => true
        case Some(value) => statement.entity == value
      }
      val aa = a match {
        case None => true
        case Some(value) => statement.attribute == value
      }
      val vv = v match {
        case None => true
        case Some(value) => statement.value == value
      }
      ee && aa && vv
    }.map { statement =>
        WanderValue.Triple(statement.entity, statement.attribute, statement.value)
    }
    Right((WanderValue.Array(filtered.toSeq), bindings))
  } else {
    Right((WanderValue.Array(Seq()), bindings))
  }
}

def handleTriple(entity: Expression, attribute: Expression, value: Expression, bindings: Bindings): Either[WanderError, (WanderValue, Bindings)] = {
  val res = for {
    entityRes <- eval(entity, bindings)
    attributeRes <- eval(attribute, bindings)
    valueRes <- eval(value, bindings)
  } yield (entityRes._1, attributeRes._1, valueRes._1)
  res match {
    case Left(value) => Left(value)
    case Right(value) => value match {
      case (WanderValue.Identifier(entity), WanderValue.Identifier(attribute), value: WanderValue) => {
        val triple: WanderValue.Triple = WanderValue.Triple(entity, attribute, value)
        bindings.addTriple(triple)
        Right(triple, bindings)
      }
      case (e: WanderValue, a: WanderValue, v: WanderValue) => handleQuery(e, a, v, "", bindings)
    }
  }
}

def handleQuad(entity: Expression, attribute: Expression, value: Expression, graph: Expression, bindings: Bindings): Either[WanderError, (WanderValue.Quad, Bindings)] = {
  for {
    entityRes <- eval(entity, bindings)
    attributeRes <- eval(attribute, bindings)
    valueRes <- eval(value, bindings)
    graphRes <- eval(graph, bindings)    
  } yield (entityRes._1, attributeRes._1, valueRes._1, graphRes._1) match {
    case (WanderValue.Identifier(entity), WanderValue.Identifier(attribute), value, WanderValue.Identifier(graph)) => {
      val quad: WanderValue.Quad = WanderValue.Quad(entity, attribute, value, graph)
      bindings.addQuad(quad)
      (quad, bindings)
    }
    case _ => ???
  }
}

def handleGrouping(
    expressions: Seq[Expression],
    bindings: Bindings
): Either[WanderError, (WanderValue, Bindings)] = {
  var error: Option[WanderError] = None
  var res: (WanderValue, Bindings) = (WanderValue.Nothing, bindings)
  val itr = expressions.iterator
  while error.isEmpty && itr.hasNext do
    eval(itr.next(), res._2) match {
      case Left(err)    => error = Some(err)
      case Right(value) => res = value
    }
  if error.isDefined then Left(error.get)
  else Right(res)
}

def handleLetExpression(
    name: Name,
    value: Expression,
    bindings: Bindings
): Either[WanderError, (WanderValue, Bindings)] = {
  var newScope = bindings.newScope()
  eval(value, newScope) match {
    case Left(value) => ???
    case Right(value) =>
      newScope = newScope.bindVariable(name, value._1)
      Right((value._1, newScope))
  }
}

def handleApplication(
    name: Name,
    arguments: Seq[Expression],
    bindings: Bindings
): Either[WanderError, (WanderValue, Bindings)] =
  bindings.read(name) match {
    case Left(err) => Left(err)
    case Right(value) =>
      value match {
        case WanderValue.Lambda(Expression.Lambda(parameters, body)) =>
          var fnScope = bindings.newScope()
          assert(arguments.size == parameters.size)
          parameters.zipWithIndex.foreach { (param, index) =>
            val argument = eval(arguments(index), bindings) match {
              case Left(value) => ???
              case Right(value) =>
                fnScope = fnScope.bindVariable(param, value._1)
            }
          }
          eval(body, fnScope)
        case WanderValue.HostFunction(fn) => fn(arguments, bindings)
        case _ => Left(WanderError(s"Could not call function ${name.name}."))
      }
  }

def handleWhenExpression(
    conditionals: Seq[(Expression, Expression)],
    bindings: Bindings
): Either[WanderError, (WanderValue, Bindings)] =
  boundary:
    conditionals.find { (conditional, _) =>
      eval(conditional, bindings) match {
        case Right((value, _)) =>
          value match {
            case WanderValue.BooleanValue(value) => value
            case _ => break(Left(WanderError("Conditionals must evaluate to Bool.")))
          }
        case Left(err) => break(Left(err))
      }
    } match {
      case None            => Left(WanderError("No matching cases."))
      case Some((_, body)) => eval(body, bindings)
    }

def handleArray(
    expressions: Seq[Expression],
    bindings: Bindings
): Either[WanderError, (WanderValue.Array, Bindings)] = {
  val res = ListBuffer[WanderValue]()
  val itre = expressions.iterator
  var continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression, bindings) match
      case Left(err)    => return Left(err)
      case Right(value) => res += value._1
  Right((WanderValue.Array(res.toList), bindings))
}

def handleSet(
    expressions: Seq[Expression],
    bindings: Bindings
): Either[WanderError, (WanderValue.Set, Bindings)] = {
  val res = ListBuffer[WanderValue]()
  val itre = expressions.iterator
  var continue = true
  while continue && itre.hasNext
  do
    val expression = itre.next()
    eval(expression, bindings) match
      case Left(err)    => return Left(err)
      case Right(value) => res += value._1
  Right((WanderValue.Set(res.toSet), bindings))
}
