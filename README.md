statsd.scala
============

statsd (https://github.com/etsy/statsd) without the node.js and flushing to ganglia instead of graphite.

= How it works

Create an instance of bitlove.statsd.StatsdDaemon:
    
    val daemon = new bitlove.statsd.StatsdDaemon(gangliaHost   = "localhost",
		  	       		         gangliaPort   = 8649,
		  	       		         listenPort    = 8125,
		  	       		         flushInterval = 60000) // ms
..and start it:

    daemon()

Then push json messages to it over UDP.

    >> require "socket"
    => true
    >> require "json"
    => true
    >> socket = UDPSocket.new 
    => #<UDPSocket:0x101236368>
    >> socket.send({:action => "timing", :name => "MyController#action", :duration => "120"}.to_json, 0, "localhost", 8125)
    => 65

There are currently 4 actions:

# Counters

increment messages look like this:

    {:action => "inc", :delta => "someIntValue", :name => "name of metric here"}

decrement messages look like this:

    {:action => "dec", :delta => "someIntValue", :name => "name of metric here"}

# Timers

messages look like this:

    {:action => "timing", :duration => "valueInMs", :name => "name of metric here"}

# LoadMeters

messages look like this:

    {:action => "mark", :count => "someIntValue", :name => "name of metric here"}

