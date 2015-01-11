package org.juitar.util.time

import java.lang.Math._

import scala.util.{Success, Try}

class TimeSampleSink(series: String, capacity: Int = 10) extends Sink[TimeSample](capacity, TimeSampleSink.validate(series)) {

  require(series != null, "series cannot be null")

  implicit val timeOrder = SampleOrdering

  @volatile private[this] var aggregate = AggregatedTimeSample(series)

  final override def add(item: TimeSample): Try[TimeSample] = {
    super.add(item) match {
      case s@Success(value) =>
        aggregate = aggregate & AggregatedTimeSample(value)
        s
      case f => f
    }
  }

  def top(n: Int) = topN(n)
  def history = lastN
  def aggr = aggregate
  def median: Long = percentile(0.5)
  def percentile90: Long = percentile(0.9)
  def percentile95: Long = percentile(0.95)
  def percentile99: Long = percentile(0.99)

  def  percentile(quantile: Double): Long = {
    require(quantile >= 0.0 && quantile <= 1.0, s"'$quantile' is out of range. Expected a number between 0.0 and 1.0")

    val freeze = lastN

    if (freeze.length == 0)  return 0


    val sorted = freeze.map(s => s.elapsed).sorted
    val n = sorted.length

    val pos = quantile * n
    if (pos >= sorted.length) return sorted(n - 1)

    sorted(pos.toInt)
  }

}
object TimeSampleSink {
  def validate(series: String)(sample: TimeSample): TimeSample =
    if (series != sample.series) throw new IncompatibleSeriesException(sample.series, series)
    else sample
}

object SampleOrdering extends Ordering[TimeSample] {
  override def compare(x: TimeSample, y: TimeSample): Int = -x.elapsed.compareTo(y.elapsed)
}
