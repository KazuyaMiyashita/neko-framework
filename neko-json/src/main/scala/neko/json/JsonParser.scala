package neko.json

import scala.util.parsing.combinator._
import scala.language.implicitConversions

object JsonParser extends JavaTokenParsers {

  def parse(input: String): Option[JsValue] = parseAll(value, input) match {
    case Success(result, _) => Some(result)
    case _                  => None
  }

  lazy val value: Parser[JsValue] = (
    obj |
      arr |
      dequotedStringLiteral ^^ {
        case string => JsString(string)
      } |
      floatingPointNumber ^^ {
        case number => JsNumber(number.toDouble)
      } |
      "null" ^^ {
        case _ => JsNull
      } |
      "true" ^^ {
        case _ => JsBoolean(true)
      } |
      "false" ^^ {
        case _ => JsBoolean(false)
      }
  )
  lazy val obj: Parser[JsObject] = "{" ~ repsep(member, ",") ~ "}" ^^ {
    case (_ ~ members ~ _) => JsObject(members.toMap)
  }
  lazy val arr: Parser[JsArray] = "[" ~ repsep(value, ",") ~ "]" ^^ {
    case (_ ~ values ~ _) => JsArray(values.to(Vector))
  }
  lazy val member: Parser[(String, JsValue)] = (dequotedStringLiteral ~ ":" ~ value) ^^ {
    case (string ~ _ ~ value) => (string, value)
  }

  lazy val dequotedStringLiteral: Parser[String] = stringLiteral ^^ { str =>
    str.substring(1, str.length - 1)
  }

}
