package org.juitar.util.time

import java.util.concurrent.TimeUnit

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.time.NoTimeConversions
import scala.concurrent.duration._

class AggregationTest extends SpecificationWithJUnit with NoTimeConversions {

   "accumulate" should {
     "calculate the correct measurement values" in {
       (Aggregation("name", 12, 10, 100, 20) & Aggregation("name", 24, 12, 50, 10)) === Aggregation("name", 16.0, 10, 100, 30)
     }

     "handle zero measurements" in {
       (Aggregation("name", 0, 0, 0, 0) & Aggregation("name", 10.0, 10, 10, 1)) === Aggregation("name", 10.0, 10, 10, 1)
     }
   }

  "average" should {
    "return average value" in {
      Aggregation("", 1.123, 1, 3, 3).average === 1.123
    }
  }
  "maxTime" should {
    "return max value in millis by default" in {
      Aggregation("", 3, 3, 3, 3).maxTime() === 3
    }
    "return max value in the specified units" in {
      Aggregation("", 3, 1, 1, 3).maxTime(TimeUnit.SECONDS) === 0.001
      Aggregation("", 3, 1, 1000, 3).maxTime(TimeUnit.SECONDS) === 1
    }
  }

  "maxDuration" should {
    "return max value as duration in millis" in {
      Aggregation("", 3, 1, 3, 3).maxDuration === 3.millis
      Aggregation("", 3, 1, 1000, 3).maxDuration === 1.second
    }
  }

  "minTime" should {
    "return min value in millis by default" in {
      Aggregation("", 3, 3, 3, 3).minTime() === 3
    }
    "return min value in the specified units" in {
      Aggregation("", 3, 1, 3, 3).minTime(TimeUnit.SECONDS) === 0.001
      Aggregation("", 3, 1000, 1001, 3).minTime(TimeUnit.SECONDS) === 1
    }
  }

  "minDuration" should {
    "return min value as duration in millis" in {
      Aggregation("", 3, 1, 3, 3).minDuration === 1.millis
      Aggregation("", 3, 1000, 1001, 3).minDuration === 1.second
    }
  }
 }
