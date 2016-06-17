package music2.management.json.converters
import music2.player.Player
import music2.player.filter.Smoother
import org.json4s.JObject
import org.json4s.JsonAST.JDouble

object SmootherConverter extends FilterConverter {

  override val identifier: String = "smoother"

  override protected def applyWithChild(child: Player, json: JObject): Option[Player] = {
    json \ "smoothness" match {
      case JDouble(smoothness) => Some(new Smoother(child, smoothness, getSpec(json)))
      case _ => None
    }
  }
}
