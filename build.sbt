val dottyVersion = "0.23.0-RC1"
val scala213Version = "2.13.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-cross",
    version := "0.1.0",

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",

    // To make the default compiler and REPL use Dotty
    scalaVersion := dottyVersion,

    // To cross compile with Dotty and Scala 2
    crossScalaVersions := Seq(dottyVersion, scala213Version)
  )
