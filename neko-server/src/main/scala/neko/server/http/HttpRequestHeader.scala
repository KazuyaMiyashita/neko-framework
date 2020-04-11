package neko.server.http

case class HttpRequestHeader(
    lines: Seq[String]
) {

  private val fields: Map[String, Seq[String]] =
    lines
      .map { line =>
        val Array(key, value) = line.split(":", 2)
        key.toLowerCase -> value.trim
      }
      .groupBy(_._1)
      .view
      .mapValues(_.map(_._2))
      .toMap

  def getField(key: String): Seq[String] = fields.get(key.toLowerCase).getOrElse(Seq.empty)

  def contentLength: Option[Int]  = getField("content-length").headOption.flatMap(_.toIntOption)
  def contentType: Option[String] = getField("content-type").headOption
  def mimeType: Option[String]    = contentType.map(_.split(";")(0))
  def charset: Option[String]     = contentType.flatMap(_.split(";", 2).lift(1)).map(_.split("=", 2)(1))
  def cookies: Map[String, String] = {
    val list: Seq[String] = getField("cookie")
    list
      .flatMap(_.split(";"))
      .map(_.trim)
      .map { line =>
        val Array(key, value) = line.split("=", 2)
        key -> value
      }
      .toMap
  }

  def asString = lines.mkString("\n")

}

object HttpRequestHeader {

  def fromString(lines: Seq[String]): HttpRequestHeader = HttpRequestHeader(lines)

}
