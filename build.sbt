import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease._

organization := "org.juitar"
name := "scala-time"
licenses := Seq("Apache 2.0" -> url("http://www.opensource.org/licenses/Apache-2.0"))
homepage := Some(url("https://github.com/sha1n/scala-time"))

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.12.4")
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:experimental.macros")

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.specs2" %% "specs2-core" % "4.3.4" % "test",
  "org.specs2" %% "specs2-junit" % "4.3.4" % "test",
  "org.specs2" %% "specs2-mock" % "4.3.4" % "test",
  "org.slf4j" % "slf4j-simple" % "1.7.7" % "test"
)

// --> publishing

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
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
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

lazy val publishAction = {
  state: State =>
    val extracted = Project.extract(state)
    val ref = extracted.get(thisProjectRef)
    extracted.runAggregated(com.typesafe.sbt.pgp.PgpKeys.publishSigned in Global in ref, state)
}

releaseSettings
releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    runTest,
    inquireVersions,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts.copy(action = publishAction),
    setNextVersion,
    commitNextVersion,
    pushChanges
)