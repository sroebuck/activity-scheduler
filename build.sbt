organization := "com.proinnovate"

name := "activity-scheduler"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
    // Date Library
    "joda-time" % "joda-time" % "2.6",
    "org.joda" % "joda-convert" % "1.7",
    "com.github.tototoshi" %% "scala-csv" % "1.1.2",
    // Logging...
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.typesafe" % "config" % "1.2.1",
    // Testing...
    "org.scalatest" %% "scalatest" % "2.2.2" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.0" % "test"
  )
