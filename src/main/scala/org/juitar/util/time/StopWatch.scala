package org.juitar.util.time

object StopWatch {

  type Elapsed = () => Long

  def start(): StopWatch.Elapsed = {
    val startTime = System.currentTimeMillis()

    () => split(startTime)
  }

  @inline private def split(startTime: Long) = {
    System.currentTimeMillis() - startTime
  }
}
