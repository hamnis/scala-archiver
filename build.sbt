organization := "net.hamnaberg"

name := "scala-archiver"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6", "2.10.5")

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.9"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
