name := """computer-database-scala"""

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  specs2 % Test,
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "org.webjars" % "jquery" % "1.12.3",
  "org.webjars" % "bootstrap" % "3.3.5",
  "org.postgresql" % "postgresql" % "9.4.1208"
)     

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

fork in run := true