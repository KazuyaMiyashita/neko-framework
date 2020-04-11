package neko.jdbc

import java.sql.Connection
import scala.util.Try

trait DBPool {
  def getConnection(): Try[Connection]
}
