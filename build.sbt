import Dependencies._

val dottyVersion = "0.23.0-RC1"
val scala213Version = "2.13.1"

lazy val commonSettings = Seq(
  // To make the default compiler and REPL use Dotty
  scalaVersion := dottyVersion,
  // To cross compile with Dotty and Scala 2
  // crossScalaVersions := Seq(dottyVersion, scala213Version),
  // scalacOptions ++= { if (isDotty.value) Seq("-language:Scala2Compat") else Nil },

  scalacOptions ++= "-deprecation" :: "-feature" :: Nil,
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-cross",
    version := "0.1.0",
    commonSettings,
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  )
  .aggregate(json, jdbc, server)


lazy val json = (project in file("neko-json"))
  .settings(
    name := "neko-json",
    organization := "com.kazmiy",
    version := "1.0.0",
    publishTo := Some(Resolver.file("file", file("mvn-repo"))),
    commonSettings,
    libraryDependencies ++= Seq(
      jsonDeps,
      testDeps
    ).flatten.map(_.withDottyCompat(scalaVersion.value)),
  )

lazy val jdbc = (project in file("neko-jdbc"))
  .settings(
    name := "neko-jdbc",
    organization := "com.kazmiy",
    version := "1.0.0",
    publishTo := Some(Resolver.file("file", file("mvn-repo"))),
    commonSettings,
    libraryDependencies ++= Seq(
      jsonDeps,
      testDeps
    ).flatten.map(_.withDottyCompat(scalaVersion.value)),
  )

lazy val server = (project in file("neko-server"))
  .settings(
    name := "neko-server",
    organization := "com.kazmiy",
    version := "1.0.0",
    publishTo := Some(Resolver.file("file", file("mvn-repo"))),
    commonSettings,
    libraryDependencies ++= Seq(
      serverDeps,
      testDeps
    ).flatten.map(_.withDottyCompat(scalaVersion.value)),
  )

lazy val sandbox = (project in file("sandbox"))
  .settings(
    name := "sandbox",
    organization := "com.kazmiy",
    version := "1.0.0",
    commonSettings,
    libraryDependencies ++= Seq(
      testDeps
    ).flatten.map(_.withDottyCompat(scalaVersion.value)),
  )
