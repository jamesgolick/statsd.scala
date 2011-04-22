require "socket"
require "rufus-json"

class Statsd
  def initialize(host = "localhost", port = 8125)
    @host   = host
    @port   = port
    @socket = UDPSocket.new
  end

  def time(stat)
    start = Time.now
    result = yield
    timing(stat, ((Time.now - start) * 1000).round)
    result
  end

  def inc(stat, delta = 1)
    announce :action => "inc", :delta => delta, :name => stat
  end

  def dec(stat, delta = 1)
    announce :action => "dec", :delta => delta, :name => stat
  end

  def mark(stat, count = 1)
    announce :action => "mark", :count => count, :name => stat
  end

  private
    def timing(name, duration)
      announce :action => "timing", :duration => duration, :name => name
    end

    def announce(hash)
      @socket.send(Rufus::Json.encode(hash), 0, @host, @port)
    end
end
