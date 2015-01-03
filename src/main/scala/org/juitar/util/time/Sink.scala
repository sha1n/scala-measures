package org.juitar.util.time

import java.util
import java.util.concurrent.ConcurrentLinkedDeque

import scala.collection.JavaConversions._

private [util] class Sink[T](capacity: Int) {
  
  private final val data: util.Deque[T] = new ConcurrentLinkedDeque[T]()

  def add(item: T): Unit = {
    data.addFirst(item)

    while (data.size() > capacity) data.removeLast()
  }

  final def ++ (item: T): Unit = add(item)

  final def lastN: Seq[T] = data.toSeq

  final def topN(n: Int)(implicit order: Ordering[T]): Seq[T] = {
    require(n <= capacity, "'n' must be less or equal to 'capacity'")

    val seq: Seq[T] = data.toList
    seq.sorted.slice(0, n)
  }

}
