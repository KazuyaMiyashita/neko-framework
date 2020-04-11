package neko.server.http

case class HttpRequestBody(private[http] val bytes: Option[Array[Byte]])

object HttpRequestBody {

  def empty                         = HttpRequestBody(None)
  def fromBytes(bytes: Array[Byte]) = HttpRequestBody(Some(bytes))

}
