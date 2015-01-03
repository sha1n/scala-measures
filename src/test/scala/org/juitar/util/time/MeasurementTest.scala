package org.juitar.util.time

import java.util.concurrent.TimeUnit

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.time.NoTimeConversions
import scala.concurrent.duration._

class MeasurementTest extends SpecificationWithJUnit with NoTimeConversions {

   "accumulate" should {
     "calculate the correct measurement values" in {
       (Measurement("name", 12, 10, 100, 20) & Measurement("name", 24, 12, 50, 10)) === Measurement("name", 16.0, 10, 100, 30)
     }

     "handle zero measurements" in {
       (Measurement("name", 0, 0, 0, 0) & Measurement("name", 10.0, 10, 10, 1)) === Measurement("name", 10.0, 0, 10, 1)
     }
   }

  "average" should {
    "return average value" in {
      Measurement("", 1.123, 1, 3, 3).average === 1.123
    }
  }
  "maxTime" should {
    "return max value in millis by default" in {
      Measurement("", 3, 3, 3, 3).maxTime() === 3
    }
    "return max value in the specified units" in {
      Measurement("", 3, 1, 1, 3).maxTime(TimeUnit.SECONDS) === 0.001
      Measurement("", 3, 1, 1000, 3).maxTime(TimeUnit.SECONDS) === 1
    }
  }

  "maxDuration" should {
    "return max value as duration in millis" in {
      Measurement("", 3, 1, 3, 3).maxDuration === 3.millis
      Measurement("", 3, 1, 1000, 3).maxDuration === 1.second
    }
  }

  "minTime" should {
    "return min value in millis by default" in {
      Measurement("", 3, 3, 3, 3).minTime() === 3
    }
    "return min value in the specified units" in {
      Measurement("", 3, 1, 3, 3).minTime(TimeUnit.SECONDS) === 0.001
      Measurement("", 3, 1000, 3, 3).minTime(TimeUnit.SECONDS) === 1
    }
  }

  "minDuration" should {
    "return min value as duration in millis" in {
      Measurement("", 3, 1, 3, 3).minDuration === 1.millis
      Measurement("", 3, 1000, 1, 3).minDuration === 1.second
    }
  }
 }
