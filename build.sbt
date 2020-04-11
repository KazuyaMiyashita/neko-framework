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


lazy val json = (project in file("neko-json"))
  .settings(
    name := "neko-json",
    commonSettings,
    libraryDependencies ++= Seq(
      jsonDeps,
      testDeps
    ).flatten.map(_.withDottyCompat(scalaVersion.value)),
  )

lazy val jdbc = (project in file("neko-jdbc"))
  .settings(
    name := "neko-jdbc",
    commonSettings,
    libraryDependencies ++= Seq(
      jsonDeps,
      testDeps
    ).flatten.map(_.withDottyCompat(scalaVersion.value)),
  )
