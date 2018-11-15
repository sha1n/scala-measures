package org.juitar.util.time

import org.juitar.util.time.TimeSampler._
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.concurrent.ExecutionContext

class BufferedReporterTest  extends SpecificationWithJUnit {

  private[this] implicit val executionContext = ExecutionContext.global

  "report" should {
    "report samples only after the buffer is full" in new Context {

      val s1 = TimeSample(Series1, 1)
      val s2 = TimeSample(Series2, 2)
      val s3 = TimeSample(Series1, 3)
      val s4 = TimeSample(Series2, 4)
      val s5 = TimeSample(Series1, 5)
      val s6 = TimeSample(Series1, 6)

      bufferedReporter.report(s1)
      bufferedReporter.report(s2)

      reported must beEmpty

      bufferedReporter.report(s3)

      reported must (contain(s1, s2, s3) and haveSize(3))

      bufferedReporter.report(s4)
      bufferedReporter.report(s5)

      reported must (contain(s1, s2, s3) and haveSize(3))

      bufferedReporter.report(s6)

      reported must (contain(s1, s2, s3, s4, s5, s6) and haveSize(6))

    }
  }

  "flush" should {
    "report all samples in the buffer immediately" in new Context {

      val s1 = TimeSample(Series1, 1)
      val s2 = TimeSample(Series2, 2)

      bufferedReporter.report(s1)
      bufferedReporter.report(s2)

      reported must beEmpty

      bufferedReporter.flush()

      reported must (contain(s1, s2) and haveSize(2))
    }

    "not fail when buffer is empty" in new Context {

      bufferedReporter.flush()

      reported must beEmpty

      val s1 = TimeSample(Series1, 1)

      bufferedReporter.report(s1)
      bufferedReporter.flush()

      reported must (contain(s1) and haveSize(1))

      bufferedReporter.flush()
    }

  }

  trait Context extends Scope {
    val Series1 = "S1"
    val Series2 = "S2"
    var reported: Seq[TimeSample] = Seq()
    implicit val report: ReportSample = s => {
      this.synchronized {
        reported = reported :+ s
      }
    }
    val bufferedReporter = new BufferedReporter(report, 3)

  }
}
