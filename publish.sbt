publishTo := {
  val v = (version in ThisBuild).value
  if (v.trim().endsWith("SNAPSHOT")) Some(Opts.resolver.sonatypeSnapshots) else Some(Opts.resolver.sonatypeStaging)
}

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

homepage := Some(new URL("http://github.com/hamnis/scala-archiver"))

startYear := Some(2014)

licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")))

scmInfo := Some(
  ScmInfo(
    new URL("http://github.com/hamnis/scala-archiver"),
    "scm:git:git://github.com/hamnis/scala-archiver.git",
    Some("scm:git:git@github.com:hamnis/scala-archiver.git")
  )
)

developers += Developer("hamnis", "Erlend Hamnaberg", "erlend@hamnaberg.net", new URL("http://twitter.com/hamnis"))

useGpg := true

disablePlugins(AetherPlugin)

enablePlugins(SignedAetherPlugin)

overridePublishSignedSettings
