package neko.json

object JsonFormatter {

  def format(js: JsValue): String = {
    def proc(js: JsValue, indent: Int): String = js match {
      case JsString(value)  => "\"" + value + "\""
      case JsNumber(value)  => if (value == value.toLong) "%d".format(value.toLong) else "%f".format(value)
      case JsBoolean(value) => if (value) "true" else "false"
      case JsObject(obj) =>
        "{\n" +
          obj
            .map {
              case (key, value) =>
                "  " * (indent + 1) + "\"" + key + "\": " + proc(value, indent + 1)
            }
            .mkString(",\n") + "\n" +
          "  " * indent + "}"
      case JsArray(arr) =>
        "[\n" +
          arr
            .map { value =>
              "  " * (indent + 1) + proc(value, indent + 1)
            }
            .mkString(",\n") + "\n" +
          "  " * indent + "]"
      case JsNull => "null"
    }
    proc(js, 0)
  }

}
