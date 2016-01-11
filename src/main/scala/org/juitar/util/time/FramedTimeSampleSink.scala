package org.juitar.util.time

import scala.concurrent.duration.Duration
import scala.util.Try

class FramedTimeSampleSink(series: String, interval: Duration, backlog: Int = Int.MaxValue) extends TimeSampleSeries {

  private[this] val sink = new TimeSampleSink(series, backlog)
  @volatile private[this] var timeSinceLastInterval = StopWatch.start()

  override def add(item: TimeSample): Try[TimeSample] = {
    if (timeSinceLastInterval() >= interval.toMillis) reset()

    sink.add(item)
  }

  override def top(n: Int): Seq[TimeSample] = sink.top(n)

  override def lastN: Seq[TimeSample] = sink.lastN

  override def reset(): Unit =
    synchronized {
      sink.reset()
      timeSinceLastInterval = StopWatch.start()
    }

  override def aggr: AggregatedTimeSample = sink.aggr
}
