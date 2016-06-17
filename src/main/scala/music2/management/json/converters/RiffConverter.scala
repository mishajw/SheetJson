package music2.management.json.converters
import music2.management.json.JsonParser
import music2.player.Player
import music2.player.composite.Riff.{PlayerDescription, PlayerDuration, PlayerSpan}
import music2.player.composite.{CompositePlayer, Riff}
import org.json4s.JsonAST.{JArray, JDouble}
import org.json4s.{JObject, JValue}

object RiffConverter extends CompositeConverter[PlayerDescription] {
  override val identifier: String = "riff"

  override protected def convertWrapped(json: JValue): Option[PlayerDescription] = json match {
    case JArray((jsonComponent: JObject) :: description) =>
      (JsonParser parseJson jsonComponent, description) match {
        case (Some(player), List(JDouble(start), JDouble(end))) =>
          Some(PlayerSpan(player, start, end))
        case (Some(player), List(JDouble(duration))) =>
          Some(PlayerDuration(player, duration))
        case _ => None
      }
    case _ => None
  }

  override protected def applyWithComponents(cs: Seq[PlayerDescription], json: JObject): Option[Player] =
    Some(new Riff(cs, getSpec(json)))
}
