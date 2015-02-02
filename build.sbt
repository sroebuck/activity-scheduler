organization := "com.proinnovate"

name := "activity-scheduler"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= {
  val akkaVersion = "2.3.6"
  val sprayVersion = "1.3.2"
  Seq(
    // Date Library
    "joda-time"           %  "joda-time" % "2.6",
    "org.joda"            %  "joda-convert" % "1.7",
    "com.typesafe.play"   %  "play-json_2.11" % "2.4.0-M2",
    "com.github.tototoshi" %% "scala-csv" % "1.1.2",
    // Spray stuff
    "io.spray"            %% "spray-can"     % sprayVersion,
    "io.spray"            %% "spray-routing" % sprayVersion,
    "com.typesafe.akka"   %% "akka-actor"    % akkaVersion,
    // Logging...
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "ch.qos.logback"      %  "logback-classic" % "1.1.2",
    "com.typesafe"        %  "config" % "1.2.1",
    // Testing...
    "org.scalatest"       %% "scalatest"      % "2.2.2" % "test",
    "org.scalacheck"      %% "scalacheck"     % "1.12.0" % "test",
    "io.spray"            %%  "spray-testkit" % sprayVersion  % "test",
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaVersion   % "test"
  )
}


parallelExecution in Test := false
