organization := "net.hamnaberg"

name := "scala-archiver"

crossScalaVersions := Seq("2.12.8", "2.11.12")

scalaVersion := crossScalaVersions.value.head

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.18"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
