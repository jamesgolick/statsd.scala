import sbt._

class StatsdProject(info: ProjectInfo) extends DefaultProject(info) {
  val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
  val jbossRepo = "JBoss Repo" at
                   "https://repository.jboss.org/nexus/content/repositories/releases"

  val metrics  = "com.yammer" %% "metrics" % "1.0.7" withSources()
  val specs    = "org.scala-tools.testing" %% "specs" % "1.6.6"
  val mockito  = "org.mockito" % "mockito-all" % "1.8.5"
  val netty    = "org.jboss.netty" % "netty" % "3.2.4.Final" withSources()
  val jerkson  = "com.codahale" %% "jerkson" % "0.1.5"
  val logula   = "com.codahale" %% "logula" % "2.0.0" withSources()
  val jmxetric = "com.specialprojectslab" % "jmxetric" % "0.0.5"

  override def mainClass = Some("bitlove.statsd.StatsdDaemon")   

  override def fork = forkRun("-Djava.util.logging.config.file=config/log.properties" :: Nil)
}
