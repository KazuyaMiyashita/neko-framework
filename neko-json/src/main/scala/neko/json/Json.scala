package neko.json

object Json {

  def str(value: String): JsValue    = JsString(value)
  def num(value: Double): JsValue    = JsNumber(value)
  def bool(value: Boolean): JsValue  = JsBoolean(value)
  def obj(value: (String, JsValue)*) = JsObject(value.toMap)
  def arr(value: JsValue*)           = JsArray(value.toVector)
  val nul                            = JsNull

  def parse(input: String): Option[JsValue]                               = JsonParser.parse(input)
  def format(value: JsValue): String                                      = JsonFormatter.format(value)
  def decode[T](js: JsValue)(implicit decoder: JsonDecoder[T]): Option[T] = decoder.decode(js)
  def encode[T](value: T)(implicit encoder: JsonEncoder[T]): JsValue      = encoder.encode(value)

}
