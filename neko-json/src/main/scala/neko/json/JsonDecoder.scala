package neko.json

trait JsonDecoder[T] {
  def decode(value: JsValue): Option[T]
}

object JsonDecoder {

  given as JsonDecoder[String] {
    override def decode(value: JsValue): Option[String] = value match {
      case JsString(v) => Some(v)
      case _               => None
    }
  }

  given as JsonDecoder[Double] {
    override def decode(value: JsValue): Option[Double] = value match {
      case JsNumber(v) => Some(v)
      case _               => None
    }
  }

  given as JsonDecoder[Int] {
    override def decode(value: JsValue): Option[Int] = value match {
      case JsNumber(v) => Some(v.toInt)
      case _               => None
    }
  }

  given as JsonDecoder[Boolean] {
    override def decode(value: JsValue): Option[Boolean] = value match {
      case JsBoolean(v) => Some(v)
      case _                => None
    }
  }

  given listDecoder[U](using decoder: JsonDecoder[U]) as JsonDecoder[List[U]] {
    def decode(value: JsValue): Option[List[U]] = value match {
      // None is returned if elements in JsArray are not aligned with U
      case JsArray(vs) =>
        vs.foldLeft[Option[List[U]]](Some(Nil)) { (acc, v) =>
          decoder.decode(v).flatMap { u =>
            acc.map(u :: _)
          }
        }
        .map(_.reverse)
      case _ => None
    }
  }

  import scala.deriving.{Mirror, productElement}
  import scala.compiletime.{constValue, erasedValue, error, summonFrom}

  inline given derived[T](using ev: Mirror.Of[T]) as JsonDecoder[T] = {
      inline ev match {
        case m: Mirror.ProductOf[T] => {
          new JsonDecoder[T] {
            override def decode(value: JsValue): Option[T] = {
              decodeElems[m.MirroredElemTypes, m.MirroredElemLabels].decode(value).map { p =>
                m.fromProduct(p.asInstanceOf[Product])
              }
            }
          }
        }
        case _ => error("derived encoder only supports case classes and objects, and enum cases")
      }
  }

  inline def decodeElems[Elems <: Tuple, Labels <: Tuple]: JsonDecoder[Elems] = {
    inline (erasedValue[Elems], erasedValue[Labels]) match {
      case (_: (e *: es), _: (l *: ls) ) => {
        val labelHead = constValue[l].asInstanceOf[String]
        val decoderHead: JsonDecoder[e] = tryDecoder[e]
        val decoderTail: JsonDecoder[es] = decodeElems[es, ls]
        new JsonDecoder[Elems] {
          override def decode(value: JsValue): Option[Elems] = {
            (value \ labelHead).getOption.flatMap(decoderHead.decode).flatMap { h =>
              decoderTail.decode(value).map(t => (h *: t).asInstanceOf[Elems])
            }
          }
        }
      }
      case _: Unit => JsonDecoder.const(()).asInstanceOf[JsonDecoder[Elems]]
    }
  }

  inline def tryDecoder[T]: JsonDecoder[T] = summonFrom {
    case decodeElem: JsonDecoder[T] => decodeElem
    case _ => error("`Decoder[$A] was not found")
  }

  def const[T](value: T): JsonDecoder[T] = new JsonDecoder[T] {
    override def decode(_a: JsValue) = Some(value)
  }

}
