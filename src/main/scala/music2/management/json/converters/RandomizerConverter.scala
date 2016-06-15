package music2.management.json.converters
import music2.management.json.JsonParser
import music2.player.Player
import music2.player.filter.Randomizer
import org.json4s.JObject
import org.json4s.JsonAST.JDouble

object RandomizerConverter extends JsonConverter {
  override val identifier: String = "randomizer"

  override def apply(json: JObject): Option[Player] = {
    println(json)

    val parameters: Seq[Option[(Player, Double)]] = for {
      JObject(obj) <- json
      ("randomness", JDouble(randomness)) <- obj
      ("child", child: JObject) <- obj
    } yield JsonParser.parseJson(child).map((_, randomness))

    parameters
      .flatten
      .headOption
      .map { case (c, r) => new Randomizer(c, r) }
  }
}
