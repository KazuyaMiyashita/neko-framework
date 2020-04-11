import sbt._

object Dependencies {

  val jsonDeps = Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
  )

  val jdbcDeps = Seq(
    "mysql" % "mysql-connector-java" % "8.0.17" % Test
  )

  val testDeps = Seq(
    "com.novocode" % "junit-interface" % "0.11" % Test,
  )

}
