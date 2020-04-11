package neko.server

import java.net.{ServerSocket, Socket, SocketException}
import java.util.concurrent.{ExecutorService, Executors}

class ServerSocketHandler(
    requestHandler: RequestHandler,
    port: Int
) extends Thread {

  val server: ServerSocket                   = new ServerSocket(port)
  val socketExecutorService: ExecutorService = Executors.newFixedThreadPool(32)
  val socketTerminator                       = new SocketTerminator

  override def run(): Unit = {
    try {
      while (!server.isClosed()) {
        val socket: Socket = server.accept()
        socketTerminator.register(socket)
        socketExecutorService.execute(new SocketHandler(socket, requestHandler, socketTerminator))
      }
    } catch {
      case _: SocketException => println("server socket closed")
    } finally {
      terminate()
    }
  }

  def terminate(): Unit = {
    if (!server.isClosed()) {
      server.close()
      socketExecutorService.shutdown()
      socketTerminator.terminateAll()
    }
  }

}
