package neko.server.http

case class HttpRequestLine(
    method: HttpMethod,
    uri: String,
    httpVersion: String
) {

  def getPath: String = {
    uri.split('?')(0)
  }

  def getQueries: Map[String, Seq[String]] = {
    def splitAmpersand(query: String): Map[String, Seq[String]] = {
      query
        .split('&')
        .toList
        .filter(_ != "")
        .flatMap { key_value =>
          key_value.split('=').toList match {
            case key :: value :: Nil if key != "" => List(key -> value)
            case _                                => Nil
          }
        }
        .groupBy(_._1)
        .view
        .mapValues(_.map(_._2))
        .toMap
    }

    uri.split('?') match {
      case Array(_, query_flagment) => {
        query_flagment.split('#') match {
          case Array(query)    => splitAmpersand(query)
          case Array(query, _) => splitAmpersand(query)
          case _               => Map.empty
        }
      }
      case _ => Map.empty
    }
  }

  def getFlagment: Option[String] = {
    uri.split('#') match {
      case Array(_, flagment) => Some(flagment)
      case _                  => None
    }
  }

  def asString: String = s"${method.asString} ${uri} ${httpVersion}"

}

object HttpRequestLine {

  def fromString(line: String): HttpRequestLine = {
    val Array(method, uri, httpVersion) = line.split(' ')
    HttpRequestLine(HttpMethod.fromString(method), uri, httpVersion)
  }

}
