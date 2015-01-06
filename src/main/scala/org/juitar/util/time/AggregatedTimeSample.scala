package org.juitar.util.time

import java.util.concurrent.TimeUnit

import scala.concurrent.duration
import scala.concurrent.duration.Duration

case class AggregatedTimeSample(series: String, avg: Double, min: Long, max: Long, count: Long) {

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

  def accumulate(m: AggregatedTimeSample): AggregatedTimeSample = {
    if (series != m.series) throw new IncompatibleSeriesException(m.series, series)

    AggregatedTimeSample(
      m.series,
      combinedAvg(m),
      if (count == 0) m.min else if (m.count == 0) min else math.min(m.min, min),
      math.max(m.max, max),
      count + m.count
    )
  }

  def & (m: AggregatedTimeSample) = accumulate(m)


  private def combinedAvg(m: AggregatedTimeSample): Double =
    if (m.count + count > 0) ((m.avg * m.count) + (avg * count)) / (m.count + count)
    else 0
}

object AggregatedTimeSample {
  def apply(series: String, time: Duration): AggregatedTimeSample = AggregatedTimeSample(series, time.toMillis, time.toMillis, time.toMillis, 1)
  def apply(series: String, time: Long): AggregatedTimeSample = AggregatedTimeSample(series, time, time, time, 1)
  def apply(sample: TimeSample): AggregatedTimeSample = AggregatedTimeSample(sample.series, sample.elapsed, sample.elapsed, sample.elapsed, 1)
  def apply(series: String): AggregatedTimeSample = AggregatedTimeSample(series, 0, 0, 0, 0)
}

