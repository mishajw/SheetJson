package music2.management.json.converters.origin

import music2.management.json.converters.JsonConverter
import music2.player.Player
import music2.player.origin.Tone
import music2.player.origin.Tone._
import music2.util.Frequencies.FrequencyOf
import music2.util.Notes
import org.json4s.JObject
import org.json4s.JsonAST.{JDouble, JString}

object ToneConverter extends JsonConverter {

  case class JTone(note: Double, waveFunction: String = "sine")

  override val identifier: String = "tone"

  override def apply(json: JObject): Option[Player] = {
    // Transform note strings into frequencies
    val transformed = json transformField {
      case ("note", JString(n)) =>
        Notes noteFor n match {
          case Some(note) => ("note", JDouble(note frequency))
          case None => "note" -> JString(n)
        }
    }

    for {
      jTone <- transformed.extractOpt[JTone]
      wave <- waveFunctions get jTone.waveFunction
    } yield new Tone(jTone.note, wave, getSpec(json))
  }

}
