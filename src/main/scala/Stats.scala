package bitlove.statsd

import com.yammer.metrics.MetricsGroup
import com.yammer.metrics.Counter
import com.yammer.metrics.Meter
import com.yammer.metrics.Timer

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ConcurrentMap
import scala.collection.JavaConversions.JConcurrentMapWrapper

class Stats {
  val metricsGroup = new MetricsGroup(this.getClass)

  val timerMetrics: ConcurrentMap[String, Timer] =
    new JConcurrentMapWrapper(new ConcurrentHashMap())
  val counterMetrics: ConcurrentMap[String, Counter] =
    new JConcurrentMapWrapper(new ConcurrentHashMap())
  val loadMeterMetrics: ConcurrentMap[String, Meter] =
    new JConcurrentMapWrapper(new ConcurrentHashMap())

  def addTiming(name: String, timeInMilliseconds: Int): Unit = {
    val timer = timerMetrics.getOrElseUpdate(name, metricsGroup.timer(name))
    timer.update(timeInMilliseconds, TimeUnit.MILLISECONDS)
  }

  def incrementCounter(name: String, delta: Long): Unit = {
    withCounter(name) { counter => counter += delta }
  }

  def decrementCounter(name: String, delta: Long): Unit = {
    withCounter(name) { counter => counter -= delta }
  }

  def markLoadMeter(name: String, count: Long): Unit = {
    val meter = loadMeterMetrics.getOrElseUpdate(name, metricsGroup.meter(name, name))
    meter.mark(count)
  }

  private def withCounter(name: String)(f: Counter => Unit): Unit = {
    f(counterMetrics.getOrElseUpdate(name, metricsGroup.counter(name)))
  }
}
