package org.juitar.util.time

import org.juitar.util.time.TimeSampler._

object Demo extends App {

  logo()

  val SeriesName = "My Action Series"
  val measurementsSink = new MeasurementSink(SeriesName, 10)

  implicit val reporter: ReportSample = demoReporter


  runDemo()

  // <-- End

  def runDemo() = {
    for (i <- 1 to 10) action(i * 10) withTimeSampleAs SeriesName

    summary(measurementsSink.aggr)
  }

  def action(sleep: Long) = Thread sleep sleep

  def demoReporter(s: TimeSample): Unit = {
    measurementsSink ++ s

    val color = s match {
      case ts@TimeSample(_, _, t) if t < 30 => Console.GREEN + Console.BOLD
      case ts@TimeSample(_, _, t) if t < 50 => Console.YELLOW + Console.BOLD
      case _ => Console.RED + Console.BOLD
    }

    println(color + s"Got sample '${s.series}' with time value of ${s.time}" + Console.RESET)
  }

  def summary(aggr: Measurement) = {
    println()
    val title = s"Summary of '${aggr.series}':"
    info(title)
    info("".padTo(title.length, "-").mkString)
    info(s"Average execution time: ${aggr.averageDuration}.\r\nMaximum execution time: ${aggr.maxDuration}.\r\nNumber of executions: ${aggr.count}.")
  }

  def info(msg: String) = println(Console.WHITE + Console.BOLD + msg + Console.RESET)

  def logo() = println(Console.WHITE + Console.BOLD +
    """
      |▓█████▄ ▓█████  ███▄ ▄███▓ ▒█████
      |▒██▀ ██▌▓█   ▀ ▓██▒▀█▀ ██▒▒██▒  ██▒
      |░██   █▌▒███   ▓██    ▓██░▒██░  ██▒
      |░▓█▄   ▌▒▓█  ▄ ▒██    ▒██ ▒██   ██░
      |░▒████▓ ░▒████▒▒██▒   ░██▒░ ████▓▒░
      | ▒▒▓  ▒ ░░ ▒░ ░░ ▒░   ░  ░░ ▒░▒░▒░
      | ░ ▒  ▒  ░ ░  ░░  ░      ░  ░ ▒ ▒░
      | ░ ░  ░    ░   ░      ░   ░ ░ ░ ▒
      |   ░       ░  ░       ░       ░ ░
      | ░
    """.stripMargin)
}
