package neko.server

import java.net.Socket

trait RequestHandler {
  def handle(socket: Socket): Unit
}
