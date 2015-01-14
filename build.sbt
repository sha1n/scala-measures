import sbt.Keys._
import sbt._

organization := "org.juitar"
name := "scala-time"
licenses := Seq("Apache 2.0" -> url("http://www.opensource.org/licenses/Apache-2.0"))
homepage := Some(url("https://github.com/sha1n/scala-time"))

scalaVersion := "2.11.2"
crossScalaVersions := Seq("2.10.4", "2.11.2")
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:experimental.macros")

libraryDependencies ++= Seq(
"org.slf4j" % "slf4j-api" % "1.7.5",
"com.newrelic.agent.java" % "newrelic-api" % "2.14.1",
"org.specs2" %% "specs2" % "2.3.12" % "test",
"org.slf4j" % "slf4j-simple" % "1.7.5" % "test"
)

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false}
pomExtra :=
  <scm>
    <url>git@github.com:sha1n/scala-time.git</url>
    <connection>scm:git:git@github.com:sha1n/scala-time.git</connection>
  </scm>
  <developers>
    <developer>
      <id>sha1n</id>
      <name>Shai Nagar</name>
      <url>https://github.com/sha1n</url>
    </developer>
  </developers>


publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}