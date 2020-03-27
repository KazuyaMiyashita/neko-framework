package neko.json

trait JsonEncoder[T] {
  def encode(value: T): JsValue
}

object JsonEncoder {

  implicit object StringEncoder extends JsonEncoder[String] {
    override def encode(value: String): JsValue = JsString(value)
  }

  implicit object DoubleEncoder extends JsonEncoder[Double] {
    override def encode(value: Double): JsValue = JsNumber(value)
  }

  implicit object IntEncoder extends JsonEncoder[Int] {
    override def encode(value: Int): JsValue = JsNumber(value.toDouble)
  }

  implicit object BooleanEncoder extends JsonEncoder[Boolean] {
    override def encode(value: Boolean): JsValue = JsBoolean(value)
  }

  given iterableEncoder[U, Iter[U] <: Iterable[U]](using encoder: JsonEncoder[U]) as JsonEncoder[Iter[U]] {
    def encode(value: Iter[U]): JsValue = JsArray(value.map(encoder.encode(_)).toVector)
  }

  implicit object NilEncoder extends JsonEncoder[Nil.type] {
    override def encode(value: Nil.type): JsValue = JsArray(Vector.empty)
  }

  given optionEncoder[U, Opt[U] <: Option[U]](using encoder: JsonEncoder[U]) as JsonEncoder[Opt[U]] {
    def encode(value: Opt[U]): JsValue = value match {
      case Some(v) => encoder.encode(v)
      case None    => JsNull
    }
  }

  implicit object NoneEncoder extends JsonEncoder[None.type] {
    override def encode(value: None.type): JsValue = JsNull
  }

  given mapEncoder[U](using encoder: JsonEncoder[U]) as JsonEncoder[Map[String, U]] {
    def encode(value: Map[String, U]): JsValue =
      JsObject(value.map { case (str, js) => str -> encoder.encode(js) })
  }

}
