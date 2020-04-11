package neko.server.http

import java.io.{InputStream, BufferedInputStream}

case class HttpRequest(
    line: HttpRequestLine,
    header: HttpRequestHeader,
    body: HttpRequestBody
) {

  def asString: String = {
    List(line.asString, header.asString, "", bodyAsString).mkString("\n")
  }

  def bodyAsString: String = body.bytes match {
    case Some(bs) => new String(bs, header.charset.getOrElse("UTF-8"))
    case None     => ""
  }

}

object HttpRequest {

  def fromInputStream(in: InputStream): HttpRequest = {
    val bin = new BufferedInputStream(in)
    def getFirstHalf(): List[String] = {
      val CS = '\r'.toByte // 10
      val LF = '\n'.toByte // 13

      @annotation.tailrec
      def loop(lines: List[String], bytes: List[Byte]): (List[String], List[Byte]) = {
        val b = bin.read().toByte
        if (b == LF)
          bytes match {
            case CS :: Nil => (lines, Nil)
            case _         => loop(new String(bytes.reverse.toArray, "UTF-8").stripLineEnd :: lines, Nil)
          }
        else loop(lines, b :: bytes)
      }
      loop(Nil, Nil)._1.reverse
    }
    val firstHalf: List[String] = getFirstHalf()

    val line   = HttpRequestLine.fromString(firstHalf.head)
    val header = HttpRequestHeader.fromString(firstHalf.tail)
    val body = header.contentLength match {
      case None => HttpRequestBody.empty
      case Some(length) => {
        val bytes = new Array[Byte](length)
        bin.read(bytes, 0, length)
        HttpRequestBody.fromBytes(bytes)
      }
    }
    HttpRequest(line, header, body)
  }

}
