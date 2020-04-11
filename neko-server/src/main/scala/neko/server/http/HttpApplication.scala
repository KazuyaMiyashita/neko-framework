package neko.server.http

trait HttpApplication {

  def handle(request: HttpRequest): HttpResponse

}
