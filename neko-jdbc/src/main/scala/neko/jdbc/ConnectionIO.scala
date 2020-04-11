package neko.jdbc

import java.sql.Connection
import scala.util.{Try, Success, Failure}
import scala.util.chaining._
import scala.language.implicitConversions

case class ConnectionIO[+E, T](private[jdbc] val run: Connection => Try[Either[E, T]]) {
  def map[U](f: T => U): ConnectionIO[E, U] = ConnectionIO(run.andThen(t => t.map(e => e.map(f))))
  def flatMap[EE >: E, U](f: T => ConnectionIO[EE, U]): ConnectionIO[EE, U] = ConnectionIO { c =>
    run(c).flatMap {
      case Left(v)  => Success(Left(v))
      case Right(v) => f(v).run(c)
    }
  }
  def leftMap[EE](f: E => EE): ConnectionIO[EE, T] = ConnectionIO(run.andThen(t => t.map(e => e.left.map(f))))
  def recover[EE >: E](pf: PartialFunction[Throwable, EE]): ConnectionIO[EE, T] = ConnectionIO { c =>
    run(c) match {
      case Failure(e) if pf.isDefinedAt(e) => Success(Left(pf(e)))
      case others                          => others
    }
  }
}

object ConnectionIO {

  def right[T](f: Connection => T): ConnectionIO[Nothing, T] = {
    ConnectionIO(conn => Try(Right(f(conn))))
  }

  def either[E, T](f: Connection => Either[E, T]): ConnectionIO[E, T] = {
    ConnectionIO(conn => Try(f(conn)))
  }

  def sequence[E, T](actions: Seq[ConnectionIO[E, T]]): ConnectionIO[E, Seq[T]] = {
    ConnectionIO { c =>
      val aio = actions.foldLeft(ConnectionIO.either[E, Seq[T]](_ => Right(Seq.empty))) {
        case (acc, io) =>
          io.run(c) match {
            case Success(Right(v)) => acc.map(v +: _)
            case Success(Left(v))  => ConnectionIO(_ => Success(Left(v)))
            case Failure(e)        => ConnectionIO(_ => Failure(e))
          }
      }

      aio.run(c).map(_.map(_.reverse))
    }
  }

}
