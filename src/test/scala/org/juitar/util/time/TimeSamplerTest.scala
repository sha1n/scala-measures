package org.juitar.util.time

import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class TimeSamplerTest extends SpecificationWithJUnit with Mockito {

  import TimeSampler._

  "TimeSampler" should {
    "measure execution time and report to specified reporter method" in new Context {

      withTimeSample("Sleepy method", {})(s => reported = syntheticSample)

      reported === syntheticSample
    }

    "report to implicitly bound reporter" in new Context {

      withTimeSample("Sleepy method", sleepy(10))

      reported must not(beNull)
      reported >= TimeSample("Sleepy method", 10) must beTrue
    }

    "ignore reporter execution time" in new Context {

      withTimeSample("Sleepy method", {})(s => { sleepy(100); reported = s })

      reported must not(beNull)
      reported < TimeSample("Sleepy method", 100) must beTrue
    }


    "support DSL like syntax" in new Context {

      sleepy(10) withTimeSampleAs "Sleepy Method"

      reported must not(beNull)
      reported >= TimeSample("Sleepy method", 10) must beTrue
    }

    "not fail when reporter throws an exception" in new Context {

      withTimeSample("Empty method", {})(s => throw new RuntimeException("I'm a dummy exception")) must not(throwA[Exception])

      reported must beNull
    }

    "not fail when reporter is null" in new Context {

      withTimeSample("Empty method", {})(null) must not(throwA[Exception])

      reported must beNull
    }
  }

  trait Context extends Scope {
    val syntheticSample = TimeSample("dummy sample", 0)
    var reported: TimeSample = _

    implicit val reporter: ReportSample = s => {
      this.reported = s
    }

    def sleepy(sleep: Long = 10): Long = {
      Thread sleep sleep
      sleep
    }

  }
}
