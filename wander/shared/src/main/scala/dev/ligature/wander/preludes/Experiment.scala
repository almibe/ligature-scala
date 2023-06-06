package dev.ligature.wander.preludes

case class Function[T](val args: T, val func: (T) => T)

val x = List(Function("hello", x => x), Function(5, x => x))
