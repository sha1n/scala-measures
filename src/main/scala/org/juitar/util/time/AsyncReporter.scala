package org.juitar.util.time

import java.util.Comparator
import java.util.concurrent.{PriorityBlockingQueue, TimeUnit}

import org.juitar.util.time.TimeSampler._

import scala.concurrent.ExecutionContext

class AsyncReporter(report: ReportSample, queueCapacity: Int, reporters: Int)(implicit ec: ExecutionContext) {

  def this(report: ReportSample)(implicit ec: ExecutionContext) = this(report, 100, 1)(ec)

  require(report != null, "report function must not be null")
  require(queueCapacity > 0, "queueCapacity must be positive")
  require(reporters > 0, "reporters must be positive")

  @volatile private[this] var halt = false
  private[this] val queue = new PriorityBlockingQueue[TimeSample](queueCapacity, TimeSampleOrderComparator)

  for (_ <- 1 to reporters) {
    ec.execute(() => {
      while (!halt) {
        val ts = queue.poll(1, TimeUnit.SECONDS)
        if (ts != null)
          report.apply(ts)
      }
    })
  }

  def report(timeSample: TimeSample): Unit = {
    if (halt) throw new IllegalStateException("This reporter has been shutdown.")

    queue.add(timeSample)
  }

  def shutdown(): Unit = {
    halt = true
    queue.clear()
  }
}
object AsyncReporter {
  def apply(report: ReportSample, queueCapacity: Int, reporterThreads: Int)(implicit ec: ExecutionContext): ReportSample =
    new AsyncReporter(report, queueCapacity, reporterThreads).report

  def apply(report: ReportSample)(implicit ec: ExecutionContext): ReportSample =
    new AsyncReporter(report).report
}

object TimeSampleOrderComparator extends Comparator[TimeSample] {
  override def compare(o1: TimeSample, o2: TimeSample): Int = {
    if (o1.timeStamp > o2.timeStamp) 1
    else if (o1.timeStamp < o2.timeStamp) -1
    else 0
  }
}
