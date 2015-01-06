package org.juitar.util.time

import java.util.concurrent.TimeUnit

import scala.compat.Platform
import scala.concurrent.duration.Duration

case class TimeSample private (series: String, timeStamp: Long, elapsed: Long) {

  def this(series: String, elapsed: Long) = this(series, System.currentTimeMillis(), elapsed)
  def this(series: String, timeStamp: Long, elapsed: Duration) = this(series, timeStamp, elapsed.toMillis)
  def this(series: String, elapsed: Duration) = this(series, elapsed.toMillis)

  require(series != null, "series cannot be null")
  require(timeStamp >= 0, "timestamp must be positive millis since 1970")
  require(elapsed >= 0, "elapsed must be non-negative")

  def > (other: TimeSample): Boolean = this.elapsed > other.elapsed
  def >= (other: TimeSample): Boolean = this.elapsed >= other.elapsed
  def < (other: TimeSample): Boolean = this.elapsed < other.elapsed
  def <= (other: TimeSample): Boolean = this.elapsed <= other.elapsed
  def == (other: TimeSample): Boolean = this.elapsed == other.elapsed

  def duration = Duration(elapsed, TimeUnit.MILLISECONDS)
}

object TimeSample {
  def apply(series: String, time: Long): TimeSample = TimeSample(series, Platform.currentTime, time)
  def apply(series: String, time: Duration): TimeSample = apply(series, Platform.currentTime, time.toMillis)
}
