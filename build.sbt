organization := "net.hamnaberg"

name := "scala-archiver"

crossScalaVersions := Seq("2.12.4", "2.11.12", "2.10.7")

scalaVersion := crossScalaVersions.value.head

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.15"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
