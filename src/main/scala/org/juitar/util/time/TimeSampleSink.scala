package org.juitar.util.time

import scala.util.{Success, Try}

class TimeSampleSink(series: String, capacity: Int = 10)
  extends Sink[TimeSample](capacity, TimeSampleSink.validate(series))
  with TimeSampleSeries {

  require(series != null, "series cannot be null")

  implicit val timeOrder = SampleOrdering

  @volatile private[this] var aggregate = AggregatedTimeSample(series)

  override def add(item: TimeSample): Try[TimeSample] = {
    super.add(item) match {
      case s@Success(value) =>
        aggregate = aggregate & AggregatedTimeSample(value)
        s
      case f => f
    }
  }

  override def reset(): Unit = {
    super.reset()
    aggregate = AggregatedTimeSample(series, 0, 0, 0, 0)
  }

  override def top(n: Int) = topN(n)
  def history = lastN
  override def aggr = aggregate

}
object TimeSampleSink {
  def validate(series: String)(sample: TimeSample): TimeSample =
    if (series != sample.series) throw new IncompatibleSeriesException(sample.series, series)
    else sample
}

object SampleOrdering extends Ordering[TimeSample] {
  override def compare(x: TimeSample, y: TimeSample): Int = -x.elapsed.compareTo(y.elapsed)
}
