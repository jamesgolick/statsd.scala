statsd.scala
============

statsd (https://github.com/etsy/statsd) without the node.js and flushing to ganglia instead of graphite (actually, flushing is pluggable, but for now, ganglia's the only option).

# Why?

If you have a service that requires you to run multiple processes on a single machine (ruby, python, php, node.js, etc), and you want to collect runtime metrics, you need some way to aggregate them on a per machine basis before pushing them in to a tool like ganglia. Statsd.scala is a metrics aggregator, loosely based on etsy's statsd (https://github.com/etsy/statsd).

I wrote statsd.scala because I wanted my stats aggregator to run on the JVM and flush to ganglia.

# How it works

Download the assembly:
    
    wget https://github.com/downloads/jamesgolick/statsd.scala/statsd.scala-assembly-0.1.0.jar

Grab the example config file (JSON), and modify it to your liking:
    
    wget https://github.com/jamesgolick/statsd.scala/raw/development/config/example.conf
    vi example.conf

You'll want to make sure that ganglia.host and ganglia.port point to your gmond instance. The log settings in the example conf may or may not work for you.

Once you have a config, start the daemon:

    CONFIG=/path/to/statsd.conf java -jar statsd.scala-assembly-0.1.0.jar

Then push json messages to it over UDP (there's also a rubygem. see below).

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

# RubyGem

The rubygem uses a facade for JSON generation that's capable of using several different JSON backends. Make sure to require your JSON lib of choice _before_ requiring statsd.

The API is pretty simple:

    sudo gem install statsd.scala

    ree-1.8.7-2011.03 :001 > require "rubygems"
      => true 
    ree-1.8.7-2011.03 :002 > require "yajl
     => true 
    ree-1.8.7-2011.03 :005 > require "statsd"
     => true 
    ree-1.8.7-2011.03 :006 > stats=Statsd.new
     => #<Statsd:0x103a65258 @socket=#<UDPSocket:0x103a651e0>, @port=8125, @host="localhost"> 
    ree-1.8.7-2011.03 :008 > stats.time("somemetric") { sleep(2); "return value of the block" }
     => "return value of the block" 
    ree-1.8.7-2011.03 :009 > stats.inc("somecounter", 10)
     => 48 
    ree-1.8.7-2011.03 :010 > stats.dec("somecounter", 10)
     => 48 
    ree-1.8.7-2011.03 :011 > stats.mark("somemeter", 10)
     => 47 

# Dependencies

* the Coda Hale stack
  * codahale/jerkson
  * codahale/metrics (all the heavy lifting is done by metrics)
  * codahale/logula
* netty
* jmxetric (because who the hell wants to implement the gmetric protocol?)

# Copyright

Copyright (c) 2011 James Golick, BitLove Inc.
