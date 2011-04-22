package bitlove.statsd.flushing

import com.codahale.logula.Logging

import com.yammer.metrics.Counter
import com.yammer.metrics.LoadMeter
import com.yammer.metrics.Timer

import ganglia.gmetric.GMetric
import ganglia.gmetric.GMetric.UDPAddressingMode
import ganglia.gmetric.GMetricType
import ganglia.gmetric.GMetricSlope

import java.util.concurrent.TimeUnit

class GMetricFlusher(host: String, port: Int, flushInterval: Int) extends Flusher with Logging {
  val gm = new GMetric(host, port, UDPAddressingMode.UNICAST)

  def flush(name: String, counter: Counter) = {
    val nameAndGroup = getNameAndGroup(name)

    announce(nameAndGroup, "count", counter.count.toString, GMetricType.UINT32, GMetricSlope.POSITIVE)
  }

  def flush(nameString: String, timer: Timer) = {
    val nameAndGroup = getNameAndGroup(nameString)

    announce(nameAndGroup, "count", timer.count.toString, GMetricType.UINT32, GMetricSlope.POSITIVE)
    announce(nameAndGroup, "max", timer.max.convert(TimeUnit.MILLISECONDS).value.toString, GMetricType.FLOAT, GMetricSlope.BOTH, "ms")
    announce(nameAndGroup, "min", timer.min.convert(TimeUnit.MILLISECONDS).value.toString, GMetricType.FLOAT, GMetricSlope.BOTH, "ms")
    announce(nameAndGroup, "mean", timer.mean.convert(TimeUnit.MILLISECONDS).value.toString, GMetricType.FLOAT, GMetricSlope.BOTH, "ms")
    announce(nameAndGroup, "median", timer.median.convert(TimeUnit.MILLISECONDS).value.toString, GMetricType.FLOAT, GMetricSlope.BOTH, "ms")
    announce(nameAndGroup, "sd", timer.standardDeviation.convert(TimeUnit.MILLISECONDS).value.toString, GMetricType.FLOAT, GMetricSlope.BOTH, "ms")
    announce(nameAndGroup, "95%", timer.p95.convert(TimeUnit.MILLISECONDS).value.toString, GMetricType.FLOAT, GMetricSlope.BOTH, "ms")
    announce(nameAndGroup, "99%", timer.p99.convert(TimeUnit.MILLISECONDS).value.toString, GMetricType.FLOAT, GMetricSlope.BOTH, "ms")
    announce(nameAndGroup, "99.9%", timer.p999.convert(TimeUnit.MILLISECONDS).value.toString, GMetricType.FLOAT, GMetricSlope.BOTH, "ms")
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

  private def announce(nameAndGroup: (String, String), suffix: String, value: String, gmetricType: GMetricType, gmetricSlope: GMetricSlope, unit: String = ""): Unit = {
    val name  = metricName(nameAndGroup._1, suffix)
    val group = nameAndGroup._2
    log.trace("Announcing %s-%s %s%s %s %s.", group, name, value, unit, gmetricType.getGangliaType, gmetricSlope)

    gm.announce(name, value, gmetricType,
                  unit, gmetricSlope, flushInterval, flushInterval,
                    group)
  }
}
