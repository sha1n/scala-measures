package org.juitar.util.time

import org.slf4j.{Logger, LoggerFactory}

object Reporters {

  private final val DefaultLogger = LoggerFactory.getLogger(TimeSample.getClass)

  type TimeSampleFormatter = (TimeSample) => String
  def defaultLogFormatter(timeSample: TimeSample): String = timeSample.toString

  def reportLog(timeSample: TimeSample)
    (implicit logger: Logger = DefaultLogger,
    formatter: TimeSampleFormatter = defaultLogFormatter): Unit = logger.info(formatter(timeSample))

  def reportConsole(timeSample: TimeSample)
    (implicit formatter: TimeSampleFormatter = defaultLogFormatter): Unit = Console.println(formatter(timeSample))

}

