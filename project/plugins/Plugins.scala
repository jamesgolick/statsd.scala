class Plugins(info: sbt.ProjectInfo) extends sbt.PluginDefinition(info) {
  val codasRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
  val jamesRepo = "James Golick's Repo" at "http://repo.jamesgolick.com/"
  val sbtRsync  = "com.codahale" % "rsync-sbt" % "0.1.1"
  val sbtRuby   = "com.bitlove" % "sbt-ruby" % "1.0.7"
}
