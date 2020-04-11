package neko.server.http

sealed trait HttpMethod {
  def asString: String
}

object HttpMethod {
  def fromString(m: String): HttpMethod = m match {
    case "GET"     => GET
    case "POST"    => POST
    case "PUT"     => PUT
    case "OPTIONS" => OPTIONS
    case _         => throw new NoSuchElementException
  }
}
object GET extends HttpMethod {
  override def asString = "GET"
}
object POST extends HttpMethod {
  override def asString = "POST"
}
object PUT extends HttpMethod {
  override def asString = "PUT"
}
object OPTIONS extends HttpMethod {
  override def asString = "OPTIONS"
}
