package neko.json

import org.junit.Test
import org.junit.Assert._

class JsonDecoderSpec {

  case class Nyan(foo: Int, bar: String)
  case class Complex(n: Int, nyan: Nyan)

  @Test def t1(): Unit = {

    val intDecoder: JsonDecoder[Int] = summon[JsonDecoder[Int]]
    assertEquals(intDecoder.decode(Json.num(42.0)), Some(42))

    val stringDecoder: JsonDecoder[String] = summon[JsonDecoder[String]]
    assertEquals(stringDecoder.decode(Json.str("Bar")), Some("Bar"))

    val nyanDecoder: JsonDecoder[Nyan] = summon[JsonDecoder[Nyan]]
    assertEquals(
      nyanDecoder.decode(
        Json.obj(
          "foo" -> Json.num(42.0),
          "bar" -> Json.str("Bar")
        )
      ),
      Some(Nyan(42, "Bar"))
    )

    val complexDecoder: JsonDecoder[Complex] = summon[JsonDecoder[Complex]]
    assertEquals(
      complexDecoder.decode(
        Json.obj(
          "n" -> Json.num(100),
          "nyan" -> Json.obj(
            "foo" -> Json.num(42.0),
            "bar" -> Json.str("Bar")
          )
        )
      ),
      Some(Complex(100, Nyan(42, "Bar")))
    )

  }
}
