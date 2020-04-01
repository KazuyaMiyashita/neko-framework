## Neko Framework

Neko Framework (Dotty), Neko means "cat" but it's not category

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

// TODO
