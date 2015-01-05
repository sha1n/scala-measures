package org.juitar.util.time

import java.util.Comparator
import java.util.concurrent.{TimeUnit, PriorityBlockingQueue, ThreadFactory, Executors}

import org.juitar.util.time.TimeSampler._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class AsyncReporter(report: ReportSample, queueCapacity: Int, reporters: Int) {

  def this(report: ReportSample) = this(report, 100, Runtime.getRuntime.availableProcessors())

  private[this] val executor = Executors.newFixedThreadPool(reporters, new ThreadFactory {
    override def newThread(r: Runnable): Thread = {
      val t = Executors.defaultThreadFactory().newThread(r)
      t.setDaemon(true)
      t
    }
  })

  private[this] implicit val executionContext = ExecutionContext.fromExecutor(executor)
  private[this] val queue = new PriorityBlockingQueue[TimeSample](queueCapacity, TimeSampleOrderComparator)

  for (i <- 1 to reporters) {
    executionContext.execute(new Runnable {
      override def run(): Unit = {
        while (!executor.isShutdown) {
          val ts = queue.poll(10, TimeUnit.MILLISECONDS)
          if (ts != null)
            report.apply(ts)
        }
      }
    })
  }

  def report(timeSample: TimeSample): Unit = {
    if (executor.isShutdown) throw new IllegalStateException("This reporter has been shutdown.")

    queue.add(timeSample)
  }

  def shutdown(timeout: Duration) = {
    executor.shutdown()
    executor.awaitTermination(timeout.toMillis, TimeUnit.MILLISECONDS)
    queue.clear()
  }
}

object TimeSampleOrderComparator extends Comparator[TimeSample] {
  override def compare(o1: TimeSample, o2: TimeSample): Int = {
    if (o1.timeStamp > o2.timeStamp) 1
    else if (o1.timeStamp < o2.timeStamp) -1
    else 0
  }
}
