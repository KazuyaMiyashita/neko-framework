package neko.json

import org.junit.Test
import org.junit.Assert._

class JsonEncoderSpec {

  case class Nyan(foo: Int, bar: String)
  case class Complex(n: Int, nyan: Nyan)

  @Test def t1(): Unit = {

    val intEncoder: JsonEncoder[Int] = summon[JsonEncoder[Int]]
    assertEquals(intEncoder.encode(42), Json.num(42.0))

    val stringEncoder: JsonEncoder[String] = summon[JsonEncoder[String]]
    assertEquals(stringEncoder.encode("Bar"), Json.str("Bar"))

    val nyanEncoder: JsonEncoder[Nyan] = summon[JsonEncoder[Nyan]]
    assertEquals(nyanEncoder.encode(
      Nyan(42, "Bar")),
      Json.obj(
        "foo" -> Json.num(42.0),
        "bar" -> Json.str("Bar")
      )
    )

    val complexEncoder: JsonEncoder[Complex] = summon[JsonEncoder[Complex]]
    assertEquals(complexEncoder.encode(
      Complex(100, Nyan(42, "Bar"))),
      Json.obj(
        "n" -> Json.num(100),
        "nyan" -> Json.obj(
          "foo" -> Json.num(42.0),
          "bar" -> Json.str("Bar")
        )
      )
    )

  }
}
