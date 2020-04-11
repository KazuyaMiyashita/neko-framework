package neko.jdbc

import java.sql.ResultSet

object query {

  def select[T](resultSet: ResultSet, mapping: ResultSet => T): Option[T] = {
    Option.when(resultSet.next())(mapping(resultSet))
  }

  def list[T](resultSet: ResultSet, mapping: ResultSet => T): List[T] = {
    Iterator.continually(resultSet).takeWhile(_.next()).map(mapping).toList
  }

}
