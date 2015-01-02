package org.juitar.util.time

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.time.NoTimeConversions

class StopWatchTest extends SpecificationWithJUnit with NoTimeConversions {


  "StopWatch" should {

    "measure block execution time" in {
      val time = StopWatch.start()

      Thread sleep 10

      val split = time()

      split must beGreaterThanOrEqualTo(10L)
      split must beLessThan(20L)

      Thread sleep 100

      val end = time()
      end must beGreaterThanOrEqualTo(110L)
    }

    "support more than one measurement at a time" in {
      val time1 = StopWatch.start()
      val time2 = StopWatch.start()

      Thread sleep 10

      val end2 = time2()
      end2 must beGreaterThanOrEqualTo(10L)

      Thread sleep 10

      val end1 = time1()
      end1 must beGreaterThanOrEqualTo(20L)
    }
  }
}
