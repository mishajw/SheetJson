package music2.management.json.converters
import music2.player.Player
import music2.player.filter.KeyActivated
import org.json4s.JObject
import org.json4s.JsonAST.JInt

object KeyActivatedConverter extends FilterConverter {
  override val identifier: String = "key-activated"

  override protected def applyWithChild(child: Player, json: JObject): Option[Player] = {
    json \ "key" match {
      case JInt(key) => Some(new KeyActivated(key.toInt, child))
      case _ => None
    }
  }
}
