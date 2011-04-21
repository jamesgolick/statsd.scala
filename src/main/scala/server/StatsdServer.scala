package bitlove.statsd.server

import bitlove.statsd.Stats

import com.codahale.logula.Logging

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import org.jboss.netty.bootstrap.ConnectionlessBootstrap
import org.jboss.netty.channel.ChannelPipeline
import org.jboss.netty.channel.ChannelPipelineFactory
import org.jboss.netty.channel.Channels
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory
import org.jboss.netty.channel.socket.DatagramChannelFactory
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory
import org.jboss.netty.handler.codec.string.StringDecoder
import org.jboss.netty.handler.codec.string.StringEncoder
import org.jboss.netty.util.CharsetUtil

class StatsdServer(stats: Stats, port: Int = 8125) extends Logging {
  def listen = {
    val f = new NioDatagramChannelFactory(Executors.newCachedThreadPool())

    val b = new ConnectionlessBootstrap(f)

    // Configure the pipeline factory.
    b.setPipelineFactory(new ChannelPipelineFactory() {
      def getPipeline(): ChannelPipeline = {
        Channels.pipeline(
          new StringEncoder(CharsetUtil.ISO_8859_1),
          new StringDecoder(CharsetUtil.ISO_8859_1),
          new StatsdServerHandler(stats))
      }
    })

    b.setOption("broadcast", "false")

    b.setOption(
            "receiveBufferSizePredictorFactory",
            new FixedReceiveBufferSizePredictorFactory(1024))

    log.info("Listening on port %d.", port)
    b.bind(new InetSocketAddress(port))
  }
}
