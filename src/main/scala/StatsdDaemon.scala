package bitlove.statsd

import bitlove.statsd.server.StatsdServer
import bitlove.statsd.flushing.GMetricFlusher
import bitlove.statsd.flushing.Flusher

class StatsdDaemon(stats: Stats, flusher: Flusher, port: Int, flushInterval: Int) {
  def apply() = {
    val flushThread = new Thread() {
      override def run = {
        stats.counterMetrics.foreach { case (k,v) => flusher.flush(k,v) }
        stats.timerMetrics.foreach { case (k,v) => flusher.flush(k,v) }
        stats.loadMeterMetrics.foreach { case (k,v) => flusher.flush(k,v) }
        Thread.sleep(flushInterval)
      }
    }
    flushThread.start

    val server = new StatsdServer(stats, port)
    server.listen
  }
}

object StatsdDaemon {
  def main(args: Array[String]): Unit = {
    val flusher = new GMetricFlusher("localhost", 8649, 60000)
    val stats   = new Stats
    val server  = new StatsdDaemon(stats, flusher, 8125, 60000)
    server()
  }
}
