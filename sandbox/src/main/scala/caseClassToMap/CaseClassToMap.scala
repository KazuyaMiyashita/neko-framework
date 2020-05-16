package sandbox.caseClassToMap

trait CaseClassToMap[T] {

  def toMap(obj: T): Map[String, Any]

}

object CaseClassToMap {


  import scala.deriving.{Mirror, productElement}
  import scala.compiletime.{erasedValue, error, summonInline}

  inline given derived[T](using ev: Mirror.Of[T]) as CaseClassToMap[T] = new CaseClassToMap[T] {
    def toMap(obj: T): Map[String, Any] = {
      inline ev match {
        case m: Mirror.ProductOf[T] => {
          val product = obj.asInstanceOf[Product]
          val keys = product.productElementNames
          val values = (0 until product.productArity).map(product.productElement)
          (keys zip values).toMap
        }
        case _ => error("CaseClassToMap only supports case classes and objects, and enum cases")
      }
    }
  }

}
