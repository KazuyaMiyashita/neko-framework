package sandbox.caseClassToMap

import org.junit.Test
import org.junit.Assert._

class CaseClassToMapSpec {


  @Test def t1(): Unit = {

    case class Nyan(foo: String, bar: Int)
    val testToMap: CaseClassToMap[Nyan] = new CaseClassToMap[Nyan] {
      override def toMap(obj: Nyan): Map[String, Any] = {
        Map(
          "foo" -> obj.foo,
          "bar" -> obj.bar
        )
      }
    }
    val nyan = Nyan("nyan", 42)
    assertEquals(
      testToMap.toMap(nyan),
      Map(
        "foo" -> "nyan",
        "bar" -> 42
      )
    )

  }

  @Test def t2(): Unit = {

    case class Nyan(foo: String, bar: Int)
    val testToMap: CaseClassToMap[Nyan] = summon[CaseClassToMap[Nyan]]
    val nyan = Nyan("nyan", 42)
    assertEquals(
      testToMap.toMap(nyan),
      Map(
        "foo" -> "nyan",
        "bar" -> 42
      )
    )

  }

}
