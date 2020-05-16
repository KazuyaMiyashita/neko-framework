package neko.fp

final case class EitherT[F[+_]: Monad, +A, +B](value: F[Either[A, B]]) {

  def map[BB](f: B => BB): EitherT[F, A, BB] = EitherT(Monad[F].map(value)(_.map(f)))
  def flatMap[AA >: A, BB](f: B => EitherT[F, AA, BB]): EitherT[F, AA, BB] = 
    EitherT(Monad[F].flatMap(value) {
      case l @ Left(_) => Monad[F].unit(l.asInstanceOf[Either[AA, BB]])
      case Right(r) => f(r).value
    })

}
