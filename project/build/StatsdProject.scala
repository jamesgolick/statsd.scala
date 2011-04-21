import sbt._

class StatsdProject(info: ProjectInfo) extends DefaultProject(info) {
  val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
  val specsRepo = "Specs Repo" at
                      "http://nexus.scala-tools.org/content/repositories/snapshots"
  val snapshotsRep = "Scala Tools Snapshots Repository" at
                          "http://scala-tools.org/repo-snapshots/"

  val jbossRepo = "JBoss Repo" at
                   "https://repository.jboss.org/nexus/content/repositories/releases"
  val fetlifeRepo = "FetLife Repo" at
                     "http://mvn.dal.fetlife"

  val metrics  = "com.yammer" %% "metrics" % "1.0.7" withSources()
  val specs    = "org.scala-tools.testing" %% "specs" % "1.6.5"
  val mockito  = "org.mockito" % "mockito-all" % "1.8.5"
  val netty    = "org.jboss.netty" % "netty" % "3.2.4.Final" withSources()
  val jerkson  = "com.codahale" %% "jerkson" % "0.1.5-SNAPSHOT"

  override def mainClass = Some("bitlove.statsd.server.StatsdServer")   
}
