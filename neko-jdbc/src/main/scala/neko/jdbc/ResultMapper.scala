package neko.jdbc

import java.sql.ResultSet

trait ColumnMapper[T] {
  def get(columnLabel: String): ResultSet => T
}

object ColumnMapper {

  given as ColumnMapper[Int] {
    override def get(columnLabel: String): ResultSet => Int = {
      _.getInt(columnLabel)
    }
  }

  given as ColumnMapper[String] {
    override def get(columnLabel: String): ResultSet => String = {
      _.getString(columnLabel)
    }
  }

}

trait RecordMapper[T] {
  def get(resultSet: ResultSet): T
}

object RecordMapper {

  // ?
  given as RecordMapper[Unit] {
    override def get(resultSet: ResultSet): Unit = ()
  }

  import scala.deriving.{Mirror, productElement}
  import scala.compiletime.{constValue, erasedValue, error, summonFrom}

  inline given derived[T](using ev: Mirror.Of[T]) as RecordMapper[T] = {
    inline ev match {
      case m: Mirror.ProductOf[T] => {
        new RecordMapper[T] {
          override def get(resultSet: ResultSet): T = {
            val t = elemsRecordMapper[m.MirroredElemTypes, m.MirroredElemLabels].get(resultSet)
            m.fromProduct(t.asInstanceOf[Product])
          }
        }
      }
      case _ => error("derived RecordMapper only supports case classes and objects, and enum cases")
    }
  }

  inline def elemsRecordMapper[Elems <: Tuple, Labels <: Tuple]: RecordMapper[Elems] = {
    inline (erasedValue[Elems], erasedValue[Labels]) match {
      case (_: (e *: es), _: (l *: ls)) => {
        val labelHead = constValue[l].asInstanceOf[String]
        val columnMapperHead: ColumnMapper[e] = tryColumnMapper[e]
        val recordMapperTail: RecordMapper[es] = elemsRecordMapper[es, ls]
        new RecordMapper[Elems] {
          override def get(resultSet: ResultSet): Elems = {
            val head: ResultSet => e = columnMapperHead.get(labelHead)
            val tail: ResultSet => es = recordMapperTail.get(_)
            (head(resultSet) *: tail(resultSet)).asInstanceOf[Elems]
          }
        }
      }
      case _: Unit => RecordMapper.const(()).asInstanceOf[RecordMapper[Elems]]
    }
  }

  inline def tryColumnMapper[T]: ColumnMapper[T] = summonFrom {
    case columnMapper: ColumnMapper[T] => columnMapper
    case _ => error("ColumnMapper[$T] was not found")
  }

  def const[T](value: T): RecordMapper[T] = new RecordMapper[T] {
    override def get(_a: ResultSet) = value
  }

}
