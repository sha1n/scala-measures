package org.juitar.util.time

import org.slf4j.LoggerFactory

import scala.util.control.Exception._

object TimeSampler {

  type ReportSample = TimeSample => Unit

  private val logger = LoggerFactory.getLogger(this.getClass)

  def withTimeSample[T <: Any](series: String, action:  => T)(implicit report: ReportSample): T = {
    val stop = StopWatch.start()
    val result = action
    val elapsed = stop()

    reportWithExceptionHandling {
      report(TimeSample(series, elapsed))
    }

    result
  }

  private def reportWithExceptionHandling =
    handling(classOf[Exception]).by {
      t => logger.warn("Failed to report measurement sample", t)
    }

  private def noOp(sample: TimeSample): Unit = {}

  implicit class TimeSamplerWithTitle[T <: Any](action: => T) {

    def withTimeSampleAs(series: => String)(implicit report: ReportSample = noOp): T = withTimeSample(series, action)
  }
}


