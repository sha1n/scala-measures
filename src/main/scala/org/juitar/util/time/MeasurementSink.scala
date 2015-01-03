package org.juitar.util.time

class MeasurementSink(series: String, capacity: Int = 10) extends Sink[Measurement](capacity) {

  implicit val averageOrder = MeasurementAverageOrdering

  @volatile private var aggregate = Measurement(series)

  override def add(m: Measurement): Unit = {
    super.add(m)
    aggregate = m & aggregate
  }

  def add(s: TimeSample): Unit = add(Measurement(s))

  def ++ (s: TimeSample): Unit = add(s)

  def aggr = aggregate
  def history = lastN
  def top(n: Int) = topN(n)
}

object MeasurementAverageOrdering extends Ordering[Measurement] {
  override def compare(x: Measurement, y: Measurement): Int = -x.average.compareTo(y.average)
}
