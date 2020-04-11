package neko.server.http

import neko.server.RequestHandler
import java.net.Socket
import java.io.{BufferedWriter, OutputStreamWriter}

class HttpRequestHandler(application: HttpApplication) extends RequestHandler {

  override def handle(socket: Socket): Unit = {
    val httpRequest: HttpRequest = HttpRequest.fromInputStream(socket.getInputStream)
    println("**request**")
    println(httpRequest.asString)

    val httpResponse = application.handle(httpRequest)

    val out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    out.write(httpResponse.asString)
    out.flush()

    println("**response**")
    println(httpResponse.asString)

    socket.close()

  }

}
