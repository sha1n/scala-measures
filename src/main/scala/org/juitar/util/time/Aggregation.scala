package org.juitar.util.time

import java.util.concurrent.TimeUnit

import scala.concurrent.duration
import scala.concurrent.duration.Duration

case class Aggregation(series: String, avg: Double, min: Long, max: Long, count: Long) {

  require(series != null, "series cannot be null")
  require(average >= 0, "time average must be non-negative")
  require(min >= 0, "min time must be non-negative")
  require(max >= 0, "max time must be non-negative")
  require(count >= 0, "count time must be non-negative")
  require(max >= min, "max cannot be smaller than min")

  def maxDuration: Duration = Duration(max, TimeUnit.MILLISECONDS)
  def maxTime(u: duration.TimeUnit = TimeUnit.MILLISECONDS): Double = Duration(max, TimeUnit.MILLISECONDS).toUnit(u)

  def minDuration: Duration = Duration(min, TimeUnit.MILLISECONDS)
  def minTime(u: duration.TimeUnit = TimeUnit.MILLISECONDS): Double = Duration(min, TimeUnit.MILLISECONDS).toUnit(u)

  def averageDuration: Duration = Duration(average, TimeUnit.MILLISECONDS)
  def average: Double = avg

  def accumulate(m: Aggregation): Aggregation = {
    require(series == m.series, "Cannot accumulate measurements with different names")

    Aggregation(
      m.series,
      combinedAvg(m),
      if (count == 0) m.min else if (m.count == 0) min else math.min(m.min, min),
      math.max(m.max, max),
      count + m.count
    )
  }

  def & (m: Aggregation) = accumulate(m)


  private def combinedAvg(m: Aggregation): Double =
    if (m.count + count > 0) ((m.avg * m.count) + (avg * count)) / (m.count + count)
    else 0
}

object Aggregation {
  def apply(series: String, time: Duration): Aggregation = Aggregation(series, time.toMillis, time.toMillis, time.toMillis, 1)
  def apply(series: String, time: Long): Aggregation = Aggregation(series, time, time, time, 1)
  def apply(sample: TimeSample): Aggregation = Aggregation(sample.series, sample.time, sample.time, sample.time, 1)
  def apply(series: String): Aggregation = Aggregation(series, 0, 0, 0, 0)
}

