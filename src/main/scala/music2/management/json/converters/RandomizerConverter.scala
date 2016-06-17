package music2.management.json.converters
import music2.player.Player
import music2.player.filter.Randomizer
import org.json4s.JObject
import org.json4s.JsonAST.JDouble

object RandomizerConverter extends FilterConverter {
  override val identifier: String = "randomizer"

  override protected def applyWithChild(child: Player, json: JObject): Option[Player] = {
    json \ "randomness" match {
      case JDouble(randomness) => Some(new Randomizer(child, randomness, getSpec(json)))
      case _ => None
    }
  }
}
