package org.juitar.util.time

import scala.util.Try

trait TimeSampleSeries {

  def add(item: TimeSample): Try[TimeSample]
  final def ++(item: TimeSample): Try[TimeSample] = add(item)
  def reset(): Unit
  def top(n: Int): Seq[TimeSample]
  def lastN: Seq[TimeSample]
  def aggr: AggregatedTimeSample

  final def percentile(quantile: Double): Long = {
    require(quantile >= 0.0 && quantile <= 1.0, s"'$quantile' is out of range. Expected a number between 0.0 and 1.0")

    val freeze = lastN

    if (freeze.isEmpty)  return 0


    val sorted = freeze.map(s => s.elapsed).sorted
    val n = sorted.length

    val pos = quantile * n
    if (pos >= sorted.length) return sorted(n - 1)

    sorted(pos.toInt)
  }

  final def median: Long = percentile(0.5)
  final def percentile90: Long = percentile(0.9)
  final def percentile95: Long = percentile(0.95)
  final def percentile99: Long = percentile(0.99)

}
