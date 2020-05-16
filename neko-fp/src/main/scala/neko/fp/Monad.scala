package neko.fp

trait Monad[F[_]] {
  def unit[A](a: => A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  def map[A, B](fa: F[A])(f: A => B): F[B] = flatMap(fa)(a => unit(f(a)))
}

object Monad {

  def apply[F[_]](implicit instance: Monad[F]): Monad[F] = instance

  implicit class MonadSyntax[F[_], A](fa: F[A])(implicit M: Monad[F]) {
    def map[B](f: A => B): F[B] = M.map(fa)(f)
    def flatMap[B](f: A => F[B]): F[B] = M.flatMap(fa)(f)
  }

  import scala.concurrent.{Future, ExecutionContext}

  given as Monad[Future] {
    def unit[A](x: => A): Future[A] = Future.successful(x)
    def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)(ExecutionContext.global)
  }

}
