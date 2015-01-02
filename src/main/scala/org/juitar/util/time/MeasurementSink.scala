package org.juitar.util.time

class MeasurementSink(series: String, capacity: Int = 10) extends Sink[Measurement](capacity) {

  @volatile private var aggregate = Measurement(series)

  override def add(m: Measurement): Unit = {
    super.add(m)
    aggregate = m & aggregate
  }

  def add(s: TimeSample): Unit = add(Measurement(s))

  def ++ (s: TimeSample): Unit = add(s)

  def aggr = aggregate
  def history = lastN
}
