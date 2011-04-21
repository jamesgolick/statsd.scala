package bitlove.statsd.flushing

import bitlove.statsd.Statsd

import com.yammer.metrics.Counter
import com.yammer.metrics.LoadMeter
import com.yammer.metrics.Timer

import ganglia.gmetric.GMetric
import ganglia.gmetric.GMetric.UDPAddressingMode
import ganglia.gmetric.GMetricType
import ganglia.gmetric.GMetricSlope

class GMetricFlusher(host: String, port: Int, flushInterval: Int) extends Flusher {
  val gm = new GMetric(host, port, UDPAddressingMode.UNICAST)

  def flush(name: String, counter: Counter) = {
    val nameAndGroup = getNameAndGroup(name)

    gm.announce(nameAndGroup._1, counter.count.toString, GMetricType.UINT32,
                  "", GMetricSlope.POSITIVE, flushInterval, flushInterval,
                    nameAndGroup._2)
  }

  def flush(nameString: String, timer: Timer) = {
    val nameAndGroup = getNameAndGroup(nameString)
    val name         = nameAndGroup._1
    val group        = nameAndGroup._2

    gm.announce(metricName(name, "count"), timer.count.toString, GMetricType.UINT32,
                  "", GMetricSlope.POSITIVE, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "max"), timer.max.toString, GMetricType.FLOAT,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "min"), timer.min.toString, GMetricType.FLOAT,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "mean"), timer.mean.toString, GMetricType.FLOAT,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "median"), timer.median.toString, GMetricType.FLOAT,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "sd"), timer.standardDeviation.toString, GMetricType.FLOAT,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "95%"), timer.p95.toString, GMetricType.FLOAT,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "99%"), timer.p99.toString, GMetricType.FLOAT,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "99.9%"), timer.p999.toString, GMetricType.FLOAT,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)
  }

  def flush(nameString: String, meter: LoadMeter) = {
    val nameAndGroup = getNameAndGroup(nameString)
    val name         = nameAndGroup._1
    val group        = nameAndGroup._2

    gm.announce(metricName(name, "one"), meter.oneMinuteRate.toString, GMetricType.UINT32,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "five"), meter.fiveMinuteRate.toString, GMetricType.UINT32,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)

    gm.announce(metricName(name, "fifteen"), meter.fifteenMinuteRate.toString, GMetricType.UINT32,
                  "", GMetricSlope.BOTH, flushInterval, flushInterval,
                    group)
  }

  private def getNameAndGroup(string: String): (String, String) = {
    val nameAndGroup = string.split("|")
    val group = nameAndGroup.length match {
      case 1 => ""
      case 2 => nameAndGroup(1)
    }
    val name = nameAndGroup(0)

    (name -> group)
  }

  private def metricName(name: String, suffix: String): String = {
    List(name, suffix).mkString("-")
  }
}
