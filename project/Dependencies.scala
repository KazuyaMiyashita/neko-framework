import sbt._

object Dependencies {

  val jsonDeps = Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
  )

  val testDeps = Seq(
    "com.novocode" % "junit-interface" % "0.11" % "test",
  )

}
