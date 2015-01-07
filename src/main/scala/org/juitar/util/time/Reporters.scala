package org.juitar.util.time

import org.slf4j.{Logger, LoggerFactory}

object Reporters {

  private final val DefaultLogger = LoggerFactory.getLogger(TimeSample.getClass)

  type TimeSampleFormatter = (TimeSample) => String

  def logReporter(timeSample: TimeSample)
                 (implicit logger: Logger = DefaultLogger,
                  formatter: TimeSampleFormatter = defaultLogFormatter): Unit =
    logger.info(formatter(timeSample))

  def consoleReporter(timeSample: TimeSample)
                     (implicit logger: Logger = DefaultLogger,
                     formatter: TimeSampleFormatter = defaultLogFormatter): Unit =
    Console.println(formatter(timeSample))

  def defaultLogFormatter(timeSample: TimeSample): String = timeSample.toString
}

