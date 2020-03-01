import Dependencies._

val catsVersion = "2.0.0"
val freesVersion = "0.8.2"
organization := "com.iv"
scalaVersion := "2.12.6"
version := "0.1.0-SNAPSHOT"
name := "cats-study2"
//libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion
libraryDependencies += "org.typelevel" %% "cats-free" % catsVersion
libraryDependencies += "org.typelevel" %% "cats-effect" % catsVersion
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"