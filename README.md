## Neko Framework

Neko Framework (Dotty), Neko means "cat" but it's not category

### maven repository

```scala
resolvers += "Github Repository" at "https://kazuyamiyashita.github.io/neko-framework/mvn-repo/",
libraryDependencies += "com.kazmiy" %% "neko-server" % "1.0.0",
libraryDependencies += "com.kazmiy" %% "neko-jdbc" % "1.0.0",
libraryDependencies += "com.kazmiy" %% "neko-json" % "1.0.0",
libraryDependencies += "com.kazmiy" %% "neko-fp" % "1.0.0",
```

### neko-json

neko-json is an AST-based JSON library that can derive Encoder and Decoder with macros.

#### Usage

```scala
case class Nyan(foo: Int, bar: String)

val nyan = Nyan(42, "Bar")
val encoder = summon[JsonEncoder[Nyan]]
val json: JsValue = encoder.encode(nyan)
// Json.obj(
//   "nyan" -> Json.obj(
//     "foo" -> Json.num(42.0),
//     "bar" -> Json.str("Bar")
//   )
// )

val decoder = summon[JsonDecoder[Nyan]]
val nyanOpt: Option[Nyan] = decoder.decode(json)
// Some(Nyan(42, "Bar"))
```


### neko-jdbc

neko-jdbc is a simple wrapper around java.sql.Connection that provides utilities for transaction composition and rollback.

#### Usage - select

```scala
import java.sql.{Connection, ResultSet, PreparedStatement}
import neko.jdbc.ConnectionIO
import neko.jdbc.query.select

case class User(userId: Long, name: String, createdAt: Instant)

class UserRepositoryImpl {
  
  def fetch(userId: Long): ConnectionIO[Nothing, Option[User]] = ConnectionIO.either { conn: Connection =>
    val query = "select * from users where user_id = ?"
    val pstmt: PreparedStatement = conn.prepareStatement(query)
    val mapping: ResultSet => MessageResponse = row =>
      User(
        id = row.getLong("id"),
        name = row.getString("name"),
        createdAt = row.getTimestamp("created_at").toInstant
      )
    select(pstmt, mapping)(conn)
  }

}

class UserService(
  userRepository: UserRepository,
  connectionIORunner: ConnectionIORunner
) {
  import UserService._

  def fetch(userId: Long): Either[FetchError, User] = {
    connectionIORunner.runReadOnly(userRepository.getch(userId)) match {
      case Success(Some(user)) => Right(user)
      case Success(None) => Left(FetchError.UserNotFound)
      case Failure(e) => Left(FetchError.Unknown(e))
    }
  }

}

object UserService {
  sealed trait FetchError
  object FetchError {
    case object UserNotFound extends FetchError
    case class Unknown(e: Throwable) extends FetchError
  }
}
```

#### Usage - insert

ConnectionIO can be composed by a for expression.
ConnectionIORunner#runTx rolls back when `Success(Left(_))` or `Failure(_)`.
ConnectionIO#recover allows you to turn exceptions into domain-defined errors.

```scala
def insertUserIO(user: User): ConnectionIO[Nothing, Unit] = ConnectionIO.right { conn =>
  val query =
    """insert into users(id, name, created_at) values (?, ?, ?);"""
  val stmt = conn.prepareStatement(query)
  stmt.setLong(1, user.id.value)
  stmt.setString(2, user.name.value)
  stmt.setTimestamp(3, Timestamp.from(user.createdAt))
  stmt.executeUpdate()
  ()
}

def insertAuthIO(auth: Auth): ConnectionIO[UserRepository.SaveNewUserError, Unit] = {
  ConnectionIO
    .right { conn =>
      val query = "insert into auths(email, hashed_password, user_id) values (?, ?, ?);"
      val pstmt = conn.prepareStatement(query)
      pstmt.setString(1, auth.email.value)
      pstmt.setString(2, auth.hashedPassword.value)
      pstmt.setString(3, auth.userId.value)
      pstmt.executeUpdate()
      ()
    }
    .recover {
      case e: SQLIntegrityConstraintViolationException if e.getErrorCode == MysqlErrorNumbers.ER_DUP_ENTRY =>
        UserRepository.SaveNewUserError.DuplicateEmail(e)
    }
}

val io: ConnectionIO[UserRepository.SaveNewUserError, User] = for {
  _ <- insertUserIO(user)
  _ <- insertAuthIO(auth)
} yield user

val dbPool = new DBPool {
  Class.forName("com.mysql.cj.jdbc.Driver")
  override def getConnection(): Connection = {
    DriverManager.getConnection(
      config.db.url,
      config.db.user,
      config.db.password
    )
  }
}
val connectionIORunner: ConnectionIORunner = DefaultConnectionIORunner(
  new DBPool {
    Class.forName("com.mysql.cj.jdbc.Driver")
    override def getConnection(): Connection = {
      DriverManager.getConnection(
        config.db.url,
        config.db.user,
        config.db.password
      )
    }
  }
)

connectionIORunner.runTx(io)
```

#### Usage - test

TestConnectionIORunner#runTx is useful for testing because it rolls back in any case.

```scala
val connectionIORunner: ConnectionIORunner = TestConnectionIORunner(
  new DBPool {
    Class.forName("com.mysql.cj.jdbc.Driver")
    override def getConnection(): Connection = {
      DriverManager.getConnection(
        config.db.url,
        config.db.user,
        config.db.password
      )
    }
  }
)

connectionIORunner.runTx(io)
```

### neko-server

A simple http server.
At this time the request response can only handle strings.

for example: https://github.com/KazuyaMiyashita/neko-server-example


### neko-fp

`Monad` and `EitherT`
