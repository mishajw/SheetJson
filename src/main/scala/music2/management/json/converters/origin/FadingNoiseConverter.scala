package music2.management.json.converters.origin

import music2.management.json.converters.JsonConverter
import music2.player.Player
import music2.player.origin.FadingNoise
import music2.util.Time.Bars
import org.json4s.JObject
import org.json4s.JsonAST.JDouble

object FadingNoiseConverter extends JsonConverter {
  override val identifier: String = "fading-noise"

  override def apply(json: JObject): Option[Player] = {
    for {
      JDouble(length) <- Option(json \ "length")
    } yield new FadingNoise(Bars(length), getSpec(json))
  }
}
