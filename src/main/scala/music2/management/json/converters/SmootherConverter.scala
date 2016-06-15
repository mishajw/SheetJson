package music2.management.json.converters
import music2.management.json.JsonParser
import music2.player.Player
import music2.player.filter.Smoother
import org.json4s.JObject

object SmootherConverter extends JsonConverter {

  case class JSmoother(child: JObject, smoothness: Double)

  override val identifier: String = "smoother"

  override def apply(json: JObject): Option[Player] = for {
    jSmoother <- json.extractOpt[JSmoother]
    child <- JsonParser.parseJson(jSmoother.child)
  } yield new Smoother(child, jSmoother.smoothness, getSpec(json))
}
