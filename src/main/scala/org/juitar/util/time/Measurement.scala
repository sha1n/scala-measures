package org.juitar.util.time

import java.util.concurrent.TimeUnit

import scala.concurrent.duration
import scala.concurrent.duration.Duration

case class Measurement(series: String, avg: Double, min: Long, max: Long, count: Long) {

  def maxDuration: Duration = Duration(max, TimeUnit.MILLISECONDS)
  def maxTime(u: duration.TimeUnit = TimeUnit.MILLISECONDS): Double = Duration(max, TimeUnit.MILLISECONDS).toUnit(u)

  def minDuration: Duration = Duration(min, TimeUnit.MILLISECONDS)
  def minTime(u: duration.TimeUnit = TimeUnit.MILLISECONDS): Double = Duration(min, TimeUnit.MILLISECONDS).toUnit(u)

  def averageDuration: Duration = Duration(average, TimeUnit.MILLISECONDS)
  def average: Double = avg

  def accumulate(m: Measurement): Measurement = {
    require(series == m.series, "Cannot accumulate measurements with different names")

    Measurement(
      m.series,
      combinedAvg(m),
      if (count == 0) m.min else if (m.count == 0) min else math.min(m.min, min),
      math.max(m.max, max),
      count + m.count
    )
  }

  def & (m: Measurement) = accumulate(m)


  private def combinedAvg(m: Measurement): Double =
    if (m.count + count > 0) ((m.avg * m.count) + (avg * count)) / (m.count + count)
    else 0
}

object Measurement {
  def apply(series: String, time: Duration): Measurement = Measurement(series, time.toMillis, time.toMillis, time.toMillis, 1)
  def apply(series: String, time: Long): Measurement = Measurement(series, time, time, time, 1)
  def apply(sample: TimeSample): Measurement = Measurement(sample.series, sample.time, sample.time, sample.time, 1)
  def apply(series: String): Measurement = Measurement(series, 0, 0, 0, 0)
}

