package org.juitar.util.time

import java.util
import java.util.concurrent.ConcurrentLinkedQueue

import org.juitar.util.time.TimeSampler._

import scala.collection.JavaConversions._

class BufferedReporter(report: ReportSample, bufferSize: Int) {

  private[this] val buffer: util.Queue[TimeSample] = new ConcurrentLinkedQueue[TimeSample]

  def report(timeSample: TimeSample): Unit = {
    buffer.add(timeSample)
    
    if(buffer.size() >= bufferSize) flush()
  }
  
  def flush() = {
    val samples = buffer.toSeq
    
    samples.foreach {
      s => if (s != null) report.apply(s)
    }
    
    buffer.removeAll(samples)
  }
  
  def clear(): Unit = {
    buffer.clear()
  }
}
object BufferedReporter {
  def apply(report: ReportSample, bufferSize: Int): ReportSample = new BufferedReporter(report, bufferSize).report
  def apply(bufferSize: Int)(report: ReportSample): ReportSample = new BufferedReporter(report, bufferSize).report
}
