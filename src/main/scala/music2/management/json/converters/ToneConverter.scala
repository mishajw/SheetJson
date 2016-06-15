package music2.management.json.converters
import music2.player.util.Frequencies.FrequencyOf
import music2.player.util.Notes
import music2.player.{Player, Tone}
import org.json4s.JObject
import org.json4s.JsonAST.{JDouble, JString}

object ToneConverter extends JsonConverter {

  case class JTone(note: Double, waveFunction: String = "sine")

  override val identifier: String = "tone"

  override def apply(json: JObject): Option[Player] = {
    // Transform note strings into frequencies
    // TODO: Handle octaves
    val transformed = json transformField {
      case ("note", JString(n)) =>
        Notes noteFor n match {
          case Some(note) => ("note", JDouble(note frequency))
          case None => "note" -> JString(n)
        }
    }

    val waveFunctionNames = Map(
      "sine" -> Tone.sine,
      "cosine" -> Tone.cosine,
      "id" -> Tone.id
    )

    transformed.extractOpt[JTone]
      .map(jTone => new Tone(jTone.note, waveFunctionNames(jTone.waveFunction), getSpec(json)))
  }
}
