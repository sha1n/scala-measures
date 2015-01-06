package org.juitar.util.time

import java.util.concurrent.{Executors, ThreadFactory}

import org.juitar.util.time.TimeSampler._

import scala.concurrent.ExecutionContext

object Demo extends App {

  logo()

  private[this] val executor = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors(), new ThreadFactory {
    override def newThread(r: Runnable): Thread = {
      val t = Executors.defaultThreadFactory().newThread(r)
      t.setDaemon(true)
      t
    }
  })
  private[this] implicit val executionContext = ExecutionContext.fromExecutor(executor)

  val SeriesName = "My Action Series"
  val timeSampleSink = new TimeSampleSink(SeriesName, 10)

  implicit val reporter: ReportSample = AsyncReporter(demoReporter)


  runDemo()

  // <-- End

  def runDemo() = {
    for (i <- 1 to 10) action(i * 10) withTimeSampleAs SeriesName

    Thread sleep 1000 // Allow the reporter queue to be consumed for the sake of this demo

    summary()
  }

  def action(sleep: Long) = Thread sleep sleep

  def demoReporter(s: TimeSample): Unit = {
    timeSampleSink ++ s

    val color = s match {
      case ts@TimeSample(_, _, t) if t < 30 => Console.GREEN + Console.BOLD
      case ts@TimeSample(_, _, t) if t < 50 => Console.YELLOW + Console.BOLD
      case _ => Console.RED + Console.BOLD
    }

    println(color + s"Got sample '${s.series}' with time value of ${s.elapsed}" + Console.RESET)
  }

  def summary() = {

    println()
    infoTitle("Top 3 Measurements:")
    info(timeSampleSink.top(3).map(m => m.duration).mkString("\r\n"))

    println()
    val aggr = timeSampleSink.aggr
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
