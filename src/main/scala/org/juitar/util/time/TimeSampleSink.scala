package org.juitar.util.time

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
  def median: Double = {
    val freeze = lastN
    val sorted = freeze.map(s => s.elapsed).sorted
    val n = sorted.length

    if (n % 2 == 0) (sorted((n / 2) - 1) + sorted(((n / 2) + 1) - 1)) / 2.0
    else sorted(((n + 1) / 2) - 1)
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
