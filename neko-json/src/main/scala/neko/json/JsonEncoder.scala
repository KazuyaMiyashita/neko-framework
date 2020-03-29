package neko.json

trait JsonEncoder[T] {
  def encode(value: T): JsValue
}

object JsonEncoder {

  given as JsonEncoder[String] {
    override def encode(value: String): JsValue = JsString(value)
  }

  given as JsonEncoder[Double] {
    override def encode(value: Double): JsValue = JsNumber(value)
  }

  given as JsonEncoder[Int] {
    override def encode(value: Int): JsValue = JsNumber(value.toDouble)
  }

  given as JsonEncoder[Boolean] {
    override def encode(value: Boolean): JsValue = JsBoolean(value)
  }

  given [U, Iter[U] <: Iterable[U]](using encoder: JsonEncoder[U]) as JsonEncoder[Iter[U]] {
    def encode(value: Iter[U]): JsValue = JsArray(value.map(encoder.encode(_)).toVector)
  }

  given nilEncoder as JsonEncoder[Nil.type] {
    override def encode(value: Nil.type): JsValue = JsArray(Vector.empty)
  }

  given optionEncoder[U, Opt[U] <: Option[U]](using encoder: JsonEncoder[U]) as JsonEncoder[Opt[U]] {
    def encode(value: Opt[U]): JsValue = value match {
      case Some(v) => encoder.encode(v)
      case None    => JsNull
    }
  }

  given mapEncoder[U](using encoder: JsonEncoder[U]) as JsonEncoder[Map[String, U]] {
    def encode(value: Map[String, U]): JsValue =
      JsObject(value.map { case (str, js) => str -> encoder.encode(js) })
  }
  
  import scala.deriving.{Mirror, productElement}
  import scala.compiletime.{erasedValue, summonInline}

  inline def encodeElem[T](elem: T): JsValue = summonInline[JsonEncoder[T]].encode(elem)

  inline def encodeElems[Elems <: Tuple](idx: Int)(value: Any): List[JsValue] =
    inline erasedValue[Elems] match {
      case _: (t *: ts) => encodeElem[t](productElement[t](value, idx)) :: encodeElems[ts](idx + 1)(value)
      case _ => Nil
    }

  inline given derived[T](using ev: Mirror.Of[T]) as JsonEncoder[T] = new JsonEncoder[T] {
    def encode(value: T): JsValue =
      inline ev match {
        case m: Mirror.ProductOf[T] => {
          val elems = encodeElems[m.MirroredElemTypes](0)(value)
          val labels = value.asInstanceOf[Product].productElementNames
          JsObject((labels zip elems).toMap)
        }
        case _ => throw new RuntimeException("derived encoder only supports case classes and objects, and enum cases")
      }
  }


}
