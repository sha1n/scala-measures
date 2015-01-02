package org.juitar.util.time

import scala.compat.Platform
import scala.concurrent.duration.Duration

case class TimeSample private (series: String, timeStamp: Long, time: Long) {

  def > (other: TimeSample): Boolean = this.time > other.time
  def >= (other: TimeSample): Boolean = this.time >= other.time
  def < (other: TimeSample): Boolean = this.time < other.time
  def <= (other: TimeSample): Boolean = this.time <= other.time
  def == (other: TimeSample): Boolean = this.time == other.time

}

object TimeSample {
  def apply(series: String, time: Long): TimeSample = TimeSample(series, Platform.currentTime, time)
  def apply(series: String, time: Duration): TimeSample = apply(series, Platform.currentTime, time.toMillis)
}
