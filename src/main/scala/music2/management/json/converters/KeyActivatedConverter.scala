package music2.management.json.converters
import music2.management.json.JsonParser
import music2.player.Player
import music2.player.filter.KeyActivated
import org.json4s.JObject
import org.json4s.JsonAST.JInt

object KeyActivatedConverter extends JsonConverter {
  override val identifier: String = "key-activated"

  override def apply(json: JObject): Option[Player] = {
    val parameters: Seq[Option[(Player, BigInt)]] = for {
      JObject(obj) <- json
      ("key", JInt(keyCode)) <- obj
      ("child", child: JObject) <- obj
    } yield JsonParser.parseJson(child).map((_, keyCode))

    parameters
      .flatten
      .headOption
      .map { case (c, r) => new KeyActivated(r.toInt, c) }
  }
}
