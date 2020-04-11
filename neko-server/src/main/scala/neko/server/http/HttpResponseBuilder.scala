package neko.server.http

case class HttpResponseBuilder(
    rules: List[HttpResponse => HttpResponse]
) {

  def withRule(rule: HttpResponse => HttpResponse): HttpResponseBuilder = {
    copy(rules = rule :: rules)
  }

  def withHeader(header: (String, String)): HttpResponseBuilder = {
    withRule(response => {
      response.copy(headers = response.headers + header)
    })
  }

  def withContentType(contentType: String): HttpResponseBuilder = {
    withHeader("Content-Type" -> contentType)
  }

  def withAllowCredentials: HttpResponseBuilder = {
    withHeader("Access-Control-Allow-Credentials" -> "true")
  }

  def withAllowControllAllowOrigin(url: String): HttpResponseBuilder = {
    withHeader("Access-Control-Allow-Origin" -> url)
  }

  def withConnectionClose: HttpResponseBuilder = {
    withHeader("Connection" -> "close")
  }

  def withContentLength: HttpResponseBuilder = {
    withRule(response => {
      response.body match {
        case Some(value) => {
          val header = "Content-Length" -> value.getBytes.length.toString
          response.copy(headers = response.headers + header)
        }
        case None => response
      }
    })
  }

  def build(status: HttpStatus): HttpResponse = {
    val response = HttpResponse(status, Map.empty, None)
    applyRules(response)
  }

  def build(status: HttpStatus, headers: Map[String, String]): HttpResponse = {
    val response = HttpResponse(status, headers, None)
    applyRules(response)
  }

  def build(status: HttpStatus, body: String): HttpResponse = {
    val response = HttpResponse(status, Map.empty, Some(body))
    applyRules(response)
  }

  def build(status: HttpStatus, headers: Map[String, String], body: String): HttpResponse = {
    val response = HttpResponse(status, headers, Some(body))
    applyRules(response)
  }

  def applyRules(response: HttpResponse): HttpResponse = {
    val rule = rules.fold(identity(_: HttpResponse))(_ andThen _)
    rule(response)
  }

}

object HttpResponseBuilder {

  lazy val default = HttpResponseBuilder(Nil).withContentLength.withConnectionClose

}
