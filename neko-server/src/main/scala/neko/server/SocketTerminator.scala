package neko.server

import scala.collection.mutable
import java.net.Socket

class SocketTerminator {

  private val sockets = mutable.Set.empty[Socket]

  def register(s: Socket): Unit = synchronized {
    sockets.add(s)
  }

  def release(s: Socket): Unit = synchronized {
    sockets.remove(s)
  }

  def terminateAll(): Unit = synchronized {
    sockets.foreach(_.close())
    sockets.clear()
  }

}
