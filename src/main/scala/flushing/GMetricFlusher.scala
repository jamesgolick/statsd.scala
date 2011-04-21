package bitlove.statsd.flushing

import bitlove.statsd.Statsd

import com.yammer.metrics.Counter
import com.yammer.metrics.LoadMeter
import com.yammer.metrics.Timer

import ganglia.gmetric.GMetric
import ganglia.gmetric.GMetric.UDPAddressingMode
import ganglia.gmetric.GMetricType
import ganglia.gmetric.GMetricSlope

class GMetricFlusher(host: String, port: Int, flushInterval: Int) {
  val gm = new GMetric(host, port, UDPAddressingMode.UNICAST)

  def flush(name: String, counter: Counter) = {
    val nameAndGroup = name.split("|")
    val group = nameAndGroup.length match {
      case 1 => ""
      case 2 => nameAndGroup(1)
    }

    gm.announce(name, counter.count.toString, GMetricType.UINT32,
                  "", GMetricSlope.POSITIVE, flushInterval, flushInterval, group)
  }
}
