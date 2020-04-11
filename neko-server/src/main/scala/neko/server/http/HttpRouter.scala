package neko.server.http

import scala.util.Try
import scala.util.matching.Regex
import neko.server.http._

case class HttpRouter(routes: Route*) {

  def handle(request: HttpRequest)(onNotFound: => HttpResponse, onError: Throwable => HttpResponse): HttpResponse = {
    Try {
      val handler: Option[HttpRequest => HttpResponse] = routes
        .find(route => route.method == request.line.method && route.url.matches(request.line.uri))
        .map(_.handler)
      handler.fold(onNotFound)(_.apply(request))
    }.fold(onError, identity)
  }

}

case class Route(method: HttpMethod, url: Regex, handler: HttpRequest => HttpResponse)

case class RouteBuilder(method: HttpMethod, url: Regex) {
  def ->(handler: HttpRequest => HttpResponse) = Route(method, url, handler)
}

object RoutingDSL {

  implicit class HttpMethodDSL(self: HttpMethod) {
    def ->(url: String) = RouteBuilder(self, url.r)
    def ->(re: Regex)   = RouteBuilder(self, re)
  }

}
