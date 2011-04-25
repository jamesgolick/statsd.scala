import sbt._

class StatsdProject(info: ProjectInfo) extends DefaultProject(info)
                                       with rsync.RsyncPublishing
                                       with ruby.GemBuilding
                                       with assembly.AssemblyBuilder {
  val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
  val jbossRepo = "JBoss Repo" at
                   "https://repository.jboss.org/nexus/content/repositories/releases"

  val metrics  = "com.yammer" %% "metrics" % "1.0.7" withSources()
  val specs    = "org.scala-tools.testing" %% "specs" % "1.6.6"
  val mockito  = "org.mockito" % "mockito-all" % "1.8.5"
  val netty    = "org.jboss.netty" % "netty" % "3.2.4.Final" withSources()
  val jerkson  = "com.codahale" %% "jerkson" % "0.1.5"
  val logula   = "com.codahale" %% "logula" % "2.1.1" withSources()
  val jmxetric = "com.specialprojectslab" % "jmxetric" % "0.0.5"
  val fig      = "com.codahale" %% "fig" % "1.1.1" withSources()

  override def mainClass = Some("bitlove.statsd.StatsdDaemon")   

  override def fork = forkRun("-Dlog4j.configuration=config/log4j.properties" :: Nil)

  /**
   * mvn repo to publish to.
   */
  def rsyncRepo = "james@jamesgolick.com:/var/www/repo.jamesgolick.com"
  override def rsyncOptions = "-rvz" :: Nil

  /**
   * Gem building stuff
   */
   override val gemAuthor = "James Golick"
   override val gemAuthorEmail = "jamesgolick@gmail.com"
   override val gemVersion = version.toString
   override val gemDependencies = Map("rufus-json" -> "0.2.5")

   lazy val publishAll = task { None } dependsOn(publish, publishGem)
}
