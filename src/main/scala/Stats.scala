package bitlove.statsd

import com.yammer.metrics.Counter
import com.yammer.metrics.LoadMeter
import com.yammer.metrics.Timer
import com.yammer.time.Duration

import java.util.concurrent.ConcurrentHashMap

import scala.collection.mutable.ConcurrentMap
import scala.collection.JavaConversions.JConcurrentMapWrapper

class Stats {
  val timerMetrics: ConcurrentMap[String, Timer] =
    new JConcurrentMapWrapper(new ConcurrentHashMap())
  val counterMetrics: ConcurrentMap[String, Counter] =
    new JConcurrentMapWrapper(new ConcurrentHashMap())
  val loadMeterMetrics: ConcurrentMap[String, LoadMeter] =
    new JConcurrentMapWrapper(new ConcurrentHashMap())

  def addTiming(name: String, timeInMilliseconds: Int): Unit = {
    val timer = timerMetrics.getOrElseUpdate(name, new Timer())
    timer += Duration.milliseconds(timeInMilliseconds)
  }

  def incrementCounter(name: String, delta: Long): Unit = {
    withCounter(name) { counter => counter.inc(delta) }
  }

  def decrementCounter(name: String, delta: Long): Unit = {
    withCounter(name) { counter => counter.dec(delta) }
  }

  def markLoadMeter(name: String, count: Long): Unit = {
    val meter = loadMeterMetrics.getOrElseUpdate(name, new LoadMeter())
    meter.mark(count)
  }

  private def withCounter(name: String)(f: Counter => Unit): Unit = {
    f(counterMetrics.getOrElseUpdate(name, new Counter(0)))
  }
}
