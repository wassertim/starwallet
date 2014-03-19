import sbt._
import Keys._
import java.nio.file.Paths
import play.Project._

name := "starbucks"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.jsoup" % "jsoup" % "1.7.2",
  "com.tzavellas" % "sse-guice" % "0.7.1"
)     

play.Project.playScalaSettings
