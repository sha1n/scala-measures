package org.juitar.util.time

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class TimeSampleSinkTest extends SpecificationWithJUnit {

  "percentile" should {
    "validate input range" in new Context {
      sink.percentile(-1) must throwA[IllegalArgumentException]
      sink.percentile(2) must throwA[IllegalArgumentException]

    }
    "work when no sample has is present" in new Context {
      sink.percentile(0.5) === 0.0
    }
    "return the correct percentile value in range" in new Context {
      sink ++ TimeSample(MeasName, 90)
      sink ++ TimeSample(MeasName, 10)
      sink ++ TimeSample(MeasName, 30)
      sink ++ TimeSample(MeasName, 40)
      sink ++ TimeSample(MeasName, 50)
      sink ++ TimeSample(MeasName, 20)
      sink ++ TimeSample(MeasName, 50)
      sink ++ TimeSample(MeasName, 70)
      sink ++ TimeSample(MeasName, 80)
      sink ++ TimeSample(MeasName, 90)

      sink.percentile(0.1) === 20
      sink.percentile(0.15) === 20
      sink.percentile(0.5) === 50
      sink.percentile(0.65) === 70
      sink.percentile(0.75) === 80
      sink.percentile(0.9) === 90
      sink.percentile(0.95) === 90
      sink.percentile(1) === 90
    }
  }

  "percentile90" should {
    "return the median value of the current history" in new Context {

      override val sink = new TimeSampleSink(MeasName, 100)

      for (i <- 1 to 100) sink ++ TimeSample(MeasName, i)

      sink.percentile(0.99) === 100
      sink.percentile(0.9) === 91
      sink.percentile(0.95) === 96
    }
  }

  "median" should {
    "return the median value of the current history" in new Context {
      sink ++ TimeSample(MeasName, 4)
      sink ++ TimeSample(MeasName, 1)
      sink ++ TimeSample(MeasName, 7)
      sink ++ TimeSample(MeasName, 2)
      sink ++ TimeSample(MeasName, 5)

      sink.median === 4
    }
  }

  "add" should {
    "fail when a time sample of a different series is added" in new Context {
      sink ++ TimeSample("!" + MeasName, 4) must beFailedTry[TimeSample]
    }
  }

  "top" should {
    "return top samples ordered by average" in new Context {
      sink ++ TimeSample(MeasName, 3)
      sink ++ TimeSample(MeasName, 2)
      sink ++ TimeSample(MeasName, 1)
      val expected = Seq(
          (sink ++ TimeSample(MeasName, 5)).get,
          (sink ++ TimeSample(MeasName, 4)).get
      )
      sink ++ TimeSample(MeasName, 1)

      sink.top(2) === expected
    }
  }

  "TimeSampleSink" should {
    "fail when null series name is provided" in {
      new TimeSampleSink(null) must throwA[IllegalArgumentException]
    }

    "fail when negative capacity is provided series name is provided" in {
      new TimeSampleSink("", 0) must throwA[IllegalArgumentException]
      new TimeSampleSink("", -1) must throwA[IllegalArgumentException]
    }
  }

  trait Context extends Scope {
    val MeasName = "Test"
    val sink = new TimeSampleSink(MeasName, 10)
  }

}
