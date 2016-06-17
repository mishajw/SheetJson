package music2.management.json.converters
import music2.management.json.JsonParser
import music2.player.Player
import music2.player.composite.Combiner
import org.json4s.{JObject, JValue}

object CombinerConverter extends CompositeConverter[Player] {
  override val identifier: String = "combiner"

  override protected def convertWrapped(json: JValue): Option[Player] = json match {
    case j: JObject => JsonParser parseJson j
    case _ => None
  }

  override protected def applyWithComponents(cs: Seq[Player], json: JObject): Option[Player] =
    Some(new Combiner( cs, getSpec(json)))
}
