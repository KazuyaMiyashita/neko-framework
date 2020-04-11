package neko.server

import java.net.{Socket, SocketException}

class SocketHandler(
    socket: Socket,
    requestHandler: RequestHandler,
    socketTerminator: SocketTerminator
) extends Thread {

  override def run(): Unit = {
    try {
      requestHandler.handle(socket)
    } catch {
      case _: SocketException => println("socket closed")
    } finally {
      socket.close()
      socketTerminator.release(socket)
    }
  }

}
