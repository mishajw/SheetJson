name := "SheetJson"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  // JSON parsing dependencies
  "org.json4s" %% "json4s-native" % "3.3.0",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  // Logging dependencies
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
)
