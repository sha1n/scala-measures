package org.juitar.util.time

class TimeSampleSink(series: String, capacity: Int = 10) extends Sink[TimeSample](capacity) {

  def history = lastN
  def median: Double = {
    val freeze = lastN
    val sorted = freeze.map(s => s.time).sorted
    val n = sorted.length

    if (n % 2 == 0) (sorted((n / 2) - 1) + sorted(((n / 2) + 1) - 1)) / 2.0
    else sorted(((n + 1) / 2) - 1)
  }

}
