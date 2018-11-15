package org.juitar.util.time

import java.util
import java.util.concurrent.ConcurrentLinkedDeque

import scala.collection.JavaConverters._
import scala.util.Try

private [util] class Sink[T](capacity: Int, validate: T => T) {

  require(capacity > 0, "capacity must be positive")

  private final val data: util.Deque[T] = new ConcurrentLinkedDeque[T]()

  def add(item: T): Try[T] = {
    Try({
      data.addFirst(validate(item))

      while (data.size() > capacity)
        data.removeLast()

      item
    })

  }

  def reset(): Unit = data.clear()

  final def lastN: Seq[T] = nullFreeData()

  final def topN(n: Int)(implicit order: Ordering[T]): Seq[T] = {
    require(n <= capacity, "'n' must be less or equal to 'capacity'")

    val seq: Seq[T] = nullFreeData()
    seq.sorted.slice(0, n)
  }


  protected def nullFreeData(additionalFilter: T => Boolean = _ => true): Seq[T] =
    data.asScala.filter {
      e => e != null && additionalFilter(e) // concurrent java collections iterators might return nulls
    }.toSeq

}
