package music2.management.json.converters.origin

import music2.management.json.converters.JsonConverter
import music2.player.Player
import music2.player.origin.Tone
import music2.player.util.Frequencies.FrequencyOf
import music2.player.util.Notes
import music2.player.util.Notes.{AbsoluteNote, Note}
import org.json4s.JObject
import org.json4s.JsonAST.{JDouble, JString}

import scala.util.Try

object ToneConverter extends JsonConverter {

  case class JTone(note: Double, waveFunction: String = "sine")

  val rRelativeNote = """([A-Za-z]{1,2})""".r
  val rAbsoluteNote = """([A-Za-z]{1,2})([\-\d]+)""".r

  override val identifier: String = "tone"

  override def apply(json: JObject): Option[Player] = {
    // Transform note strings into frequencies
    val transformed = json transformField {
      case ("note", JString(n)) =>
        noteOf(n) match {
          case Some(note) => ("note", JDouble(note frequency))
          case None => "note" -> JString(n)
        }
    }

    transformed.extractOpt[JTone]
      .map(jTone => new Tone(jTone.note, waveFunctionNames(jTone.waveFunction), getSpec(json)))
  }

  def noteOf(str: String): Option[Note] = {
    val parts = str match {
      case rRelativeNote(key) =>
        (Notes noteFor key, None)
      case rAbsoluteNote(key, octave) =>
        (Notes noteFor key, Try(octave.toInt).toOption)
      case _ =>
        (None, None)
    }

    parts match {
      case (Some(key), Some(octave)) =>
        Some(AbsoluteNote(key, octave))
      case (keyOpt, _) => keyOpt
    }
  }

  val waveFunctionNames = Map(
    "sine" -> Tone.sine,
    "cosine" -> Tone.cosine,
    "id" -> Tone.id
  )
}
