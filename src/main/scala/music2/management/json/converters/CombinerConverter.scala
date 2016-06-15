package music2.management.json.converters
import music2.player.Player
import music2.player.composite.Combiner
import org.json4s.JObject

object CombinerConverter extends JsonConverter {
  override val identifier: String = "combiner"

  override def apply(json: JObject): Option[Player] = {
    Some(new Combiner(
      getComponents(json),
      getSpec(json)))
  }
}
