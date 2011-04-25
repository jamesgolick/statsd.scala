package bitlove.statsd

import bitlove.statsd.server.StatsdServer
import bitlove.statsd.flushing.GMetricFlusher
import bitlove.statsd.flushing.Flusher

import com.codahale.fig.Configuration
import com.codahale.logula.Logging

import org.apache.log4j.Level

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
    val configFile    = Option(System.getenv.get("CONFIG"))

    if (configFile == None) {
      System.err.println("CONFIG=/path/to/config is required to start statsd.")
      System.exit(1)
    }

    val config        = new Configuration(configFile.get)

    val gangliaHost   = config("ganglia.host").or("127.0.0.1")
    val gangliaPort   = config("ganglia.port").or(8649)
    val flushInterval = config("flush_interval").or(60000)
    val port          = config("port").or(8125)

    Logging.configure { log =>
      log.registerWithJMX = true

      log.level = Level.toLevel(config("log.level").or("TRACE"))

      log.file.enabled = true
      log.file.filename = config("log.file").or("log/development.log")
      log.file.maxSize = 10 * 1024
      log.file.retainedFiles = 5
    }

    val server = new StatsdDaemon(gangliaHost, gangliaPort, port, flushInterval)

    server()
  }
}
