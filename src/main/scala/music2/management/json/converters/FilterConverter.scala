package music2.management.json.converters

import music2.management.json.JsonParser
import music2.player.Player
import org.json4s.JObject

trait FilterConverter extends JsonConverter {
  final def apply(json: JObject): Option[Player] = {
    val children: Seq[Option[Player]] = for {
      JObject(obj) <- json
      ("child", child: JObject) <- obj
    } yield JsonParser.parseJson(child)

    children.flatten.headOption match {
      case Some(child) => applyWithChild(child, json)
      case None => None
    }
  }

  protected def applyWithChild(child: Player, json: JObject): Option[Player]
}
