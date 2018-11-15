package org.juitar.util.time

import java.util.concurrent.TimeUnit

import org.specs2.mutable.SpecificationWithJUnit
import scala.concurrent.duration._

class AggregatedTimeSampleTest extends SpecificationWithJUnit {

  "AggregatedTimeSample" should {
    "fail when null series is provided" in {
      AggregatedTimeSample(null.asInstanceOf[String]) must throwA[IllegalArgumentException]
    }
    "fail when negative time is provided is provided" in {
      AggregatedTimeSample("", -1) must throwA[IllegalArgumentException]
    }
    "fail when negative average is provided" in {
      AggregatedTimeSample("", -1, 1, 2, 2) must throwA[IllegalArgumentException]
    }
    "fail when negative min is provided" in {
      AggregatedTimeSample("", 1, -1, 2, 2) must throwA[IllegalArgumentException]
    }
    "fail when negative max is provided" in {
      AggregatedTimeSample("", 1, 1, -2, 2) must throwA[IllegalArgumentException]
    }
    "fail when negative count is provided" in {
      AggregatedTimeSample("", -1, 1, 2, -2) must throwA[IllegalArgumentException]
    }
    "fail when min is greater than max" in {
      AggregatedTimeSample("", 1, 2, 1, 2) must throwA[IllegalArgumentException]
    }
  }

   "accumulate" should {
     "calculate the correct measurement values" in {
       (AggregatedTimeSample("name", 12, 10, 100, 20) & AggregatedTimeSample("name", 24, 12, 50, 10)) === AggregatedTimeSample("name", 16.0, 10, 100, 30)
     }

     "handle zero measurements" in {
       (AggregatedTimeSample("name", 0, 0, 0, 0) & AggregatedTimeSample("name", 10.0, 10, 10, 1)) === AggregatedTimeSample("name", 10.0, 10, 10, 1)
     }
   }

  "average" should {
    "return average value" in {
      AggregatedTimeSample("", 1.123, 1, 3, 3).average === 1.123
    }
  }
  "maxTime" should {
    "return max value in millis by default" in {
      AggregatedTimeSample("", 3, 3, 3, 3).maxTime() === 3
    }
    "return max value in the specified units" in {
      AggregatedTimeSample("", 3, 1, 1, 3).maxTime(TimeUnit.SECONDS) === 0.001
      AggregatedTimeSample("", 3, 1, 1000, 3).maxTime(TimeUnit.SECONDS) === 1
    }
  }

  "maxDuration" should {
    "return max value as duration in millis" in {
      AggregatedTimeSample("", 3, 1, 3, 3).maxDuration === 3.millis
      AggregatedTimeSample("", 3, 1, 1000, 3).maxDuration === 1.second
    }
  }

  "minTime" should {
    "return min value in millis by default" in {
      AggregatedTimeSample("", 3, 3, 3, 3).minTime() === 3
    }
    "return min value in the specified units" in {
      AggregatedTimeSample("", 3, 1, 3, 3).minTime(TimeUnit.SECONDS) === 0.001
      AggregatedTimeSample("", 3, 1000, 1001, 3).minTime(TimeUnit.SECONDS) === 1
    }
  }

  "minDuration" should {
    "return min value as duration in millis" in {
      AggregatedTimeSample("", 3, 1, 3, 3).minDuration === 1.millis
      AggregatedTimeSample("", 3, 1000, 1001, 3).minDuration === 1.second
    }
  }
 }
