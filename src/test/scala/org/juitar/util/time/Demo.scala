package org.juitar.util.time

import org.juitar.util.time.TimeSampler._
import scala.concurrent.duration._

object Demo extends App {

  logo()

  val SeriesName = "My Action Series"
  val measurementsSink = new MeasurementSink(SeriesName, 10)

  val asyncReporter = new AsyncReporter(demoReporter)
  implicit val reporter: ReportSample = asyncReporter.report


  runDemo()

  // <-- End

  def runDemo() = {
    for (i <- 1 to 10) action(i * 10) withTimeSampleAs SeriesName

    asyncReporter.shutdown(1.second)
    summary()
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

  def summary() = {

    println()
    infoTitle("Top 3 Measurements:")
    info(measurementsSink.top(3).map(m => m.averageDuration.toString).mkString("\r\n"))

    println()
    val aggr = measurementsSink.aggr
    infoTitle(s"Summary of '${aggr.series}':")
    info(
      s"""
         |Average execution time: ${aggr.averageDuration}.
         |Minimum execution time: ${aggr.minDuration}.
         |Maximum execution time: ${aggr.maxDuration}.
         |Number of executions: ${aggr.count}.
       """.stripMargin
    )
  }

  def infoTitle(title: String) = {
    info(title)
    info("".padTo(title.length, "-").mkString)
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
