import Dependencies._

resolvers += ("typesafe-releases" at "http://repo.typesafe.com/typesafe/releases").withAllowInsecureProtocol(true)
resolvers += ("mvn-arts" at "https://mvnrepository.com/artifact/").withAllowInsecureProtocol(true)

val catsVersion = "2.0.0"
val freesVersion = "0.8.2"
organization := "com.iv"
scalaVersion := "2.12.10"
version := "0.1.0-SNAPSHOT"
name := "cats-study2"
val scalaCacheVersion = "0.28.0"
//libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion
libraryDependencies += "org.typelevel" %% "cats-free" % catsVersion
libraryDependencies += "org.typelevel" %% "cats-effect" % catsVersion
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
// https://mvnrepository.com/artifact/org.typelevel/cats-mtl-core
libraryDependencies += "org.typelevel" %% "cats-mtl-core" % "0.7.0"
val fs2Deps = Seq(
  "co.fs2" %% "fs2-reactive-streams" % "2.1.0"
)
libraryDependencies ++= fs2Deps
libraryDependencies += "io.monix" %% "monix" % "3.1.0"
// https://mvnrepository.com/artifact/com.github.fd4s/fs2-kafka
libraryDependencies += "com.github.fd4s" %% "fs2-kafka" % "1.0.0"
// https://mvnrepository.com/artifact/com.twitter/chill
libraryDependencies += "com.twitter" %% "chill-bijection" % "0.9.5"
javacOptions ++= Seq("-encoding", "UTF-8")
libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceDirectory in Compile)(_ / "scala" / "integration" / "kafka" / "serOn" / "protobuf" / "model").value
)
libraryDependencies += "com.lihaoyi" %% "upickle" % "0.9.5"
libraryDependencies += "com.github.cb372" %% "scalacache-cats-effect" % scalaCacheVersion
libraryDependencies += "com.github.cb372" %% "scalacache-redis" % scalaCacheVersion
libraryDependencies += "com.github.cb372" %% "scalacache-guava" % scalaCacheVersion
