package neko.jdbc

import org.junit.Test
import org.junit.Assert._

class ResultMapperSpec {

  case class Nyan(foo: Int)
  // case class Nyan(foo: Int, bar: String)
  // case class Complex(n: Int, nyan: Nyan)

  @Test def t1(): Unit = {

    // val intMapper: ColumnMapper[Int] = summon[ColumnMapper[Int]]
    val nyanMapper: RecordMapper[Nyan] = summon[RecordMapper[Nyan]]
    // val unitMapper: RecordMapper[Unit] = summon[RecordMapper[Unit]]

  }

}
