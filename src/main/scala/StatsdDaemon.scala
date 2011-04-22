package bitlove.statsd

import bitlove.statsd.server.StatsdServer
import bitlove.statsd.flushing.GMetricFlusher
import bitlove.statsd.flushing.Flusher

import com.codahale.logula.Logging

class StatsdDaemon(stats:         Stats,
                   flusher:       Flusher,
                   port:          Int,
                   flushInterval: Int) extends Logging {

  def this(gangliaHost: String,
           gangliaPort: Int,
           listenPort: Int,
           flushInterval: Int) = {
    this(new Stats,
         new GMetricFlusher(gangliaHost, gangliaPort, flushInterval),
         listenPort,
         flushInterval)
  }

  def apply() = {
    val flushThread = new Thread() {
      override def run = {
        while(true) {
          try {
            Thread.sleep(flushInterval)

            log.info("Starting flush.")
            stats.counterMetrics.foreach { case (k,v) => flusher.flush(k,v) }
            stats.timerMetrics.foreach { case (k,v) => flusher.flush(k,v) }
            stats.loadMeterMetrics.foreach { case (k,v) => flusher.flush(k,v) }
            log.info("Flush complete!")
          } catch {
            case t: Throwable => log.error(t, "Exception in flush thread")
          }
        }
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
