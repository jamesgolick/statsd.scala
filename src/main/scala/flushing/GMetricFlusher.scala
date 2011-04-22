package bitlove.statsd.flushing

import com.codahale.logula.Logging

import com.yammer.metrics.Counter
import com.yammer.metrics.LoadMeter
import com.yammer.metrics.Timer

import ganglia.gmetric.GMetric
import ganglia.gmetric.GMetric.UDPAddressingMode
import ganglia.gmetric.GMetricType
import ganglia.gmetric.GMetricSlope

class GMetricFlusher(host: String, port: Int, flushInterval: Int) extends Flusher with Logging {
  val gm = new GMetric(host, port, UDPAddressingMode.UNICAST)

  def flush(name: String, counter: Counter) = {
    val nameAndGroup = getNameAndGroup(name)

    announce(nameAndGroup, "count", counter.count.toString, GMetricType.UINT32, GMetricSlope.POSITIVE)
  }

  def flush(nameString: String, timer: Timer) = {
    val nameAndGroup = getNameAndGroup(nameString)

    announce(nameAndGroup, "count", timer.count.toString, GMetricType.UINT32, GMetricSlope.POSITIVE)
    announce(nameAndGroup, "max", timer.max.toString, GMetricType.FLOAT, GMetricSlope.BOTH)
    announce(nameAndGroup, "min", timer.min.toString, GMetricType.FLOAT, GMetricSlope.BOTH)
    announce(nameAndGroup, "mean", timer.mean.toString, GMetricType.FLOAT, GMetricSlope.BOTH)
    announce(nameAndGroup, "median", timer.median.toString, GMetricType.FLOAT, GMetricSlope.BOTH)
    announce(nameAndGroup, "sd", timer.standardDeviation.toString, GMetricType.FLOAT, GMetricSlope.BOTH)
    announce(nameAndGroup, "95%", timer.p95.toString, GMetricType.FLOAT, GMetricSlope.BOTH)
    announce(nameAndGroup, "99%", timer.p99.toString, GMetricType.FLOAT, GMetricSlope.BOTH)
    announce(nameAndGroup, "99.9%", timer.p999.toString, GMetricType.FLOAT, GMetricSlope.BOTH)
  }

  def flush(nameString: String, meter: LoadMeter) = {
    val nameAndGroup = getNameAndGroup(nameString)

    announce(nameAndGroup, "one", meter.oneMinuteRate.toString, GMetricType.UINT32, GMetricSlope.BOTH)
    announce(nameAndGroup, "five", meter.fiveMinuteRate.toString, GMetricType.UINT32, GMetricSlope.BOTH)
    announce(nameAndGroup, "fifteen", meter.fifteenMinuteRate.toString, GMetricType.UINT32, GMetricSlope.BOTH)
  }

  private def getNameAndGroup(string: String): (String, String) = {
    val nameAndGroup = string.split('|')
    val group = nameAndGroup.length match {
      case 1 => ""
      case 2 => nameAndGroup(0)
    }
    val name = nameAndGroup.length match {
      case 1 => nameAndGroup(0)
      case 2 => nameAndGroup(1)
    }
 
    (name -> group)
  }

  private def metricName(name: String, suffix: String): String = {
    List(name, suffix).filter(_ != "").mkString("-")
  }

  private def announce(nameAndGroup: (String, String), suffix: String, value: String, gmetricType: GMetricType, gmetricSlope: GMetricSlope): Unit = {
    val name  = metricName(nameAndGroup._1, suffix)
    val group = nameAndGroup._2
    log.fine("Announcing %s-%s %s %s %s.", group, name, value, gmetricType.getGangliaType, gmetricSlope)

    gm.announce(name, value, gmetricType,
                  "", gmetricSlope, flushInterval, flushInterval,
                    group)
  }
}
