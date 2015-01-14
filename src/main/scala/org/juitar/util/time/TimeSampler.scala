package org.juitar.util.time

import com.newrelic.api.agent.{NewRelic, Trace}
import org.slf4j.LoggerFactory

import scala.util.control.Exception._

object TimeSampler {

  type ReportSample = TimeSample => Unit

  private val logger = LoggerFactory.getLogger(this.getClass)

  @Trace(dispatcher = true) def withTimeSample[T <: Any](series: String, action: => T)(implicit report: ReportSample): T = {
    val stop = StopWatch.start()
    val result = action
    val elapsed = stop()

    reportWithExceptionHandling {
      val timeSample = TimeSample(series, elapsed)

      report(timeSample)

      val metricName = s"/${timeSample.series}"
      NewRelic.setTransactionName(null , metricName)
      NewRelic.recordMetric(s"$metricName/time", timeSample.elapsed)
      NewRelic.incrementCounter(s"$metricName/count")
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


