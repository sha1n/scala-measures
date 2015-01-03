package org.juitar.util.time

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class MeasurementSinkTest extends SpecificationWithJUnit {

  "add" should {
    "update aggregate" in new Context {
      sink.aggr === Measurement(MeasName, 0, Long.MaxValue, 0, 0)
      sink ++ Measurement(MeasName, 1)
      sink.aggr === Measurement(MeasName, 1, 1, 1, 1)
    }

    "maintain history sequence" in new Context {
      sink ++ Measurement(MeasName, 5)
      sink ++ Measurement(MeasName, 4)
      sink ++ Measurement(MeasName, 3)
      sink ++ Measurement(MeasName, 2)
      sink ++ Measurement(MeasName, 1)

      sink.aggr === Measurement(MeasName, 3, 1, 5, 5)
      sink.lastN must haveSize(3)
      sink.lastN(0) === Measurement(MeasName, 1)
      sink.lastN(1) === Measurement(MeasName, 2)
      sink.lastN(2) === Measurement(MeasName, 3)
    }
  }

  "top" should {
    "return top measurements ordered by average" in new Context {
      sink ++ Measurement(MeasName, 5)
      sink ++ Measurement(MeasName, 4)
      sink ++ Measurement(MeasName, 3)
      sink ++ Measurement(MeasName, 2)
      sink ++ Measurement(MeasName, 1)

      sink.top(2) === Seq(Measurement(MeasName, 3), Measurement(MeasName, 2))
    }
  }

  trait Context extends Scope {
    val MeasName = "Test"
    val sink = new MeasurementSink(MeasName, 3)
  }
}
