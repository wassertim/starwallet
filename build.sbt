import sbt._
import Keys._
import play.Project._

name := "starwallet"

version := "1.0-SNAPSHOT"

playAssetsDirectories <+= baseDirectory / "public/app"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.jsoup" % "jsoup" % "1.7.2",
  "mysql" % "mysql-connector-java" % "5.1.31",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "com.tzavellas" % "sse-guice" % "0.7.1",
  "com.yuvimasory" % "jerkson_2.10" % "0.6.1",
  "com.typesafe.slick" % "slick_2.10" % "2.0.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "javax.mail" % "mail" % "1.4.7",
  "net.sf.barcode4j" % "barcode4j" % "2.1"
)     

play.Project.playScalaSettings
