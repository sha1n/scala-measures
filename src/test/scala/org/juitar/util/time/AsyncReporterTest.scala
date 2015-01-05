package org.juitar.util.time

import org.juitar.util.time.TimeSampler._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import org.specs2.time.NoTimeConversions

class AsyncReporterTest extends SpecificationWithJUnit with NoTimeConversions {

  private[this] implicit val executionContext = ExecutionContext.global

  "report" should {
    "report samples in order of timestamp values" in new Context {

      val s1 = TimeSample(Series1, 1)
      val s2 = TimeSample(Series2, 2)
      val s3 = TimeSample(Series1, 3)

      asyncReporter.report(s1)
      asyncReporter.report(s2)
      asyncReporter.report(s3)

      reported must eventually(20, 100.millis) {
        contain(s1, s2, s3)
      }
    }

    "fail after shutdown" in new Context {
      asyncReporter.shutdown()
      asyncReporter.report(TimeSample(Series1, 1)) must throwA[IllegalStateException]
    }
  }

  trait Context extends Scope {
    val Series1 = "S1"
    val Series2 = "S2"
    var reported: Seq[TimeSample] = Seq()
    implicit val reporter: ReportSample = s => {
      this.synchronized {
        reported = reported :+ s
      }
    }
    val asyncReporter = new AsyncReporter(reporter)

  }
}
