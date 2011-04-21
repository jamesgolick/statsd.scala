package bitlove.statsd.flushing

import com.yammer.metrics.Counter
import com.yammer.metrics.LoadMeter
import com.yammer.metrics.Timer

trait Flusher {
  def flush(name: String, timer: Timer): Unit
  def flush(name: String, counter: Counter): Unit
  def flush(name: String, loadMeter: LoadMeter): Unit
}
