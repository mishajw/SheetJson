package music2.management.json.converters
import music2.management.json.JsonParser
import music2.player.Player
import music2.player.composite.Riff
import music2.player.composite.Riff.{PlayerDescription, PlayerDuration, PlayerSpan}
import org.json4s.JObject
import org.json4s.JsonAST.{JArray, JDouble}

object RiffConverter extends JsonConverter {
  override val identifier: String = "riff"

  override def apply(json: JObject): Option[Player] = {
    val descriptions: Seq[Option[PlayerDescription]] = for {
      JObject(obj) <- json
      ("components", JArray(components)) <- obj
      JArray((jsonComponent: JObject) :: description) <- components
    } yield (JsonParser parseJson jsonComponent, description) match {
      case (Some(player), List(JDouble(start), JDouble(end))) =>
        Some(PlayerSpan(player, start, end))
      case (Some(player), List(JDouble(duration))) =>
        Some(PlayerDuration(player, duration))
      case _ => None
    }

    Some(new Riff(descriptions.flatten, getSpec(json)))
  }
}
