package neko.jdbc

import java.sql.Connection
import scala.util.{Try, Success}
import scala.util.chaining._
import scala.language.implicitConversions

abstract class ConnectionIORunner {

  protected final def run[E, T](io: ConnectionIO[E, T]): Connection => Try[Either[E, T]] = io.run
  def runTx[E, T](io: ConnectionIO[E, T]): Try[Either[E, T]]
  def runReadOnly[E, T](io: ConnectionIO[E, T]): Try[Either[E, T]]

}

class DefaultConnectionIORunner(dbPool: DBPool) extends ConnectionIORunner {

  override def runTx[E, T](io: ConnectionIO[E, T]): Try[Either[E, T]] = {
    dbPool.getConnection().flatMap { conn =>
      Try {
        run(io)(conn).tap {
          case Success(v) if v.isRight => conn.commit()
          case _                       => conn.rollback()
        }
      }.flatten.tap(_ => conn.close())
    }
  }

  override def runReadOnly[E, T](io: ConnectionIO[E, T]): Try[Either[E, T]] = {
    dbPool.getConnection().flatMap { conn =>
      Try {
        conn.setReadOnly(true)
        run(io)(conn)
      }.flatten.tap(_ => conn.close())
    }
  }

}

class TestConnectionIORunner(dbPool: DBPool) extends ConnectionIORunner {

  override def runTx[E, T](io: ConnectionIO[E, T]): Try[Either[E, T]] = {
    dbPool.getConnection().flatMap { conn =>
      Try {
        conn.setAutoCommit(false)
        run(io)(conn).tap(_ => conn.rollback())
      }.flatten.tap(_ => conn.close())
    }
  }

  override def runReadOnly[E, T](io: ConnectionIO[E, T]): Try[Either[E, T]] = {
    dbPool.getConnection().flatMap { conn =>
      Try {
        conn.setReadOnly(true)
        run(io)(conn)
      }.flatten.tap(_ => conn.close())
    }
  }

}
