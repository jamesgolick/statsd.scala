package bitlove.statsd.server

import bitlove.statsd.Statsd

import com.codahale.jerkson.Json._

import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.ExceptionEvent
import org.jboss.netty.channel.MessageEvent
import org.jboss.netty.channel.SimpleChannelUpstreamHandler

class StatsdServerHandler(stats: Statsd)
  extends SimpleChannelUpstreamHandler {
  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val msg    = e.getMessage.asInstanceOf[String]
    val metric = parse[Map[String, String]](msg)

    metric("action") match {
      case "inc" => 
        stats.incrementCounter(metric("name"), metric("delta").toLong)
      case "dec" =>
        stats.decrementCounter(metric("name"), metric("delta").toLong)
      case "timing" =>
        stats.addTiming(metric("name"), metric("duration").toInt)
      case "mark" =>
        stats.markLoadMeter(metric("name"), metric("count").toInt)
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) = {
    e.getCause.printStackTrace
  } 
}
