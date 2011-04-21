package bitlove.statsd

import com.yammer.metrics.Timer
import com.yammer.time.Duration

import java.util.concurrent.ConcurrentHashMap

import scala.collection.mutable.ConcurrentMap
import scala.collection.JavaConversions.JConcurrentMapWrapper

class Statsd {
  val timerMetrics: ConcurrentMap[String, Timer] = new JConcurrentMapWrapper(new ConcurrentHashMap())

  def addTiming(name: String, timeInMilliseconds: Int): Unit = {
    val timer = timerMetrics.getOrElseUpdate(name, new Timer())
    timer += Duration.milliseconds(timeInMilliseconds)
  }
}
