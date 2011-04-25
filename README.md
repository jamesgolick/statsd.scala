statsd.scala
============

statsd (https://github.com/etsy/statsd) without the node.js and flushing to ganglia instead of graphite (actually, flushing is pluggable, but for now, ganglia's the only option).

# How it works

Download the assembly:
    
    wget https://github.com/jamesgolick/statsd.scala/downloads/whatever.jar

Grab the example config file (JSON), and modify it to your liking:
    
    wget https://github.com/jamesgolick/statsd.scala/raw/development/config/example.conf
    vi example.conf

You'll want to make sure that ganglia.host and ganglia.port point to your gmond instance. The log settings in the example conf may or may not work for you.

Once you have a config, start the daemon:

    CONFIG=/path/to/statsd.conf java -jar statsd-assembly-0.0.1.jar

Then push json messages to it over UDP.

    >> require "socket"
    => true
    >> require "json"
    => true
    >> socket = UDPSocket.new 
    => #<UDPSocket:0x101236368>
    >> socket.send({:action => "timing", :name => "MyController#action", :duration => "120"}.to_json, 0, "localhost", 8125)
    => 65

There are currently 3 kinds of metrics:

### Counters

Increment messages look like this:

    {:action => "inc", :delta => "someIntValue", :name => "name of metric here"}

Decrement messages look like this:

    {:action => "dec", :delta => "someIntValue", :name => "name of metric here"}

A counter will only flush a "count" metric to ganglia.

### Timers

Messages look like this:

    {:action => "timing", :duration => "valueInMs", :name => "name of metric here"}

Timers will flush a "count" metric to ganglia, as well as statistics about the timings (min, max, mean, median, stddev, 95%, 99% 99.9%)

### LoadMeters

Messages look like this:

    {:action => "mark", :count => "someIntValue", :name => "name of metric here"}

Load meters will flush, one, five, and fifteen minute weighted meters (like `top`).


# Dependencies

* the Coda Hale stack
  * codahale/jerkson
  * codahale/metrics (all the heavy lifting is done by metrics)
  * codahale/logula
* netty
* jmxetric (because who the hell wants to implement the gmetric protocol?)

# Copyright

Copyright (c) 2011 James Golick, BitLove Inc.
