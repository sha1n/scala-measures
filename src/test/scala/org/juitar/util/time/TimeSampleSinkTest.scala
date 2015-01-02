package org.juitar.util.time

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class TimeSampleSinkTest extends SpecificationWithJUnit {

  "median" should {

    "return the median value of the current history" in new Context {
      sink ++ TimeSample(MeasName, 4)
      sink ++ TimeSample(MeasName, 1)
      sink ++ TimeSample(MeasName, 7)
      sink ++ TimeSample(MeasName, 2)
      sink ++ TimeSample(MeasName, 5)

      println(sink.history.mkString(", "))
      sink.median === 4
    }

    "return the median value of the current history with even number" in new Context {
      sink ++ TimeSample(MeasName, 4)
      sink ++ TimeSample(MeasName, 1)
      sink ++ TimeSample(MeasName, 7)
      sink ++ TimeSample(MeasName, 2)
      sink ++ TimeSample(MeasName, 5)
      sink ++ TimeSample(MeasName, 8)

      sink.median === 4.5
    }
  }

  trait Context extends Scope {
    val MeasName = "Test"
    val sink = new TimeSampleSink(MeasName, 6)
  }
}
