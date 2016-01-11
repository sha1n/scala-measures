package org.juitar.util.time

import java.util.concurrent.{Executors, ThreadFactory}

import org.juitar.util.time.TimeSampler._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random
import scala.util.{Failure, Success}

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

  final val SeriesName = "My Action Series"
  final val FramedTimeSampleSink = new FramedTimeSampleSink(SeriesName, 1.second)

  // Composing a buffered reporter with an async reporter using a single reporting thread
  implicit final val Reporter: ReportSample =
    BufferedReporter(bufferSize = 5)(AsyncReporter(report = demoReporter, queueCapacity = 10, reporterThreads = 1))


  runDemo()

  // <-- End

  def runDemo() = {
    val rand = new Random()
    for (i <- 1 to 100) action((i * rand.nextDouble()).toLong) withTimeSampleAs SeriesName

    Thread sleep 1000 // Allow the reporter queue to be consumed for the sake of this demo

    summary()
  }

  def action(sleep: Long) = Thread sleep sleep

  def demoReporter(s: TimeSample): Unit = {
    FramedTimeSampleSink ++ s match {
      case Failure(t) => error(s"Failed to report sample. Message: ${t.getMessage}")
      case Success(_) =>
    }

    val color = s match {
      case ts@TimeSample(_, _, t) if t < 30 => Console.GREEN + Console.BOLD
      case ts@TimeSample(_, _, t) if t < 50 => Console.YELLOW + Console.BOLD
      case _ => Console.RED + Console.BOLD
    }

    println(color + s"Got sample '${s.series}' with time value of ${s.duration}" + Console.RESET)
  }

  def summary() = {

    println()
    infoTitle("Top 3 Measurements:")
    info(FramedTimeSampleSink.top(3).map(m => m.duration).mkString("\r\n"))

    println()
    infoTitle("Percentiles:")
    info(s"99% - ${FramedTimeSampleSink.percentile99.millis}")
    info(s"95% - ${FramedTimeSampleSink.percentile95.millis}")
    info(s"90% - ${FramedTimeSampleSink.percentile90.millis}")
    info(s"50% - ${FramedTimeSampleSink.median.millis}")

    println()
    val aggr = FramedTimeSampleSink.aggr
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

  def info(msg: String) = colorMsg(Console.WHITE)(msg)
  def error(msg: String) = colorMsg(Console.RED)(msg)
  def colorMsg(color: String)(msg: String) = println(color + Console.BOLD + msg + Console.RESET)

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
