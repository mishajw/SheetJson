package sheetjson.management.json.converters

import org.json4s.JObject
import org.json4s.JsonAST.{JDouble, JString}
import sheetjson.player.WaveFunction
import sheetjson.player.origin.{FadingNoise, RawFile, Tone}
import sheetjson.util.Frequencies.FrequencyOf
import sheetjson.util.Notes
import sheetjson.util.Time.Bars

package object origin {

  /**
    * Convert to `Tone`
    */
  implicit object ToneConverter extends JsonConverter[Tone] {

    case class JTone(note: Double, waveFunction: String = "sine")

    override def applyOpt(json: JObject): Option[Tone] = {
      // Transform note strings into frequencies
      val transformed = json transformField {
        case ("note", JString(n)) =>
          Notes noteFor n match {
            case Some(note) => ("note", JDouble(note frequency))
            case None => "note" -> JString(n)
          }
      }

      for {
        note <- (transformed \ "note").extractOpt[Double]
        waveFunction = (transformed \ "wave_function").extractOrElse("sine": String)
        wave <- WaveFunction getOpt waveFunction
      } yield new Tone(note, wave, getSpec(json))
    }
  }

  /**
    * Convert to `FadingNoise`
    */
  implicit object FadingNoiseConverter extends JsonConverter[FadingNoise] {
    override def applyOpt(json: JObject): Option[FadingNoise] = json \ "length" match {
      case JDouble(length) => Some(new FadingNoise(Bars(length), getSpec(json)))
      case _ => None
    }
  }

  /**
    * Convert to `RawFile`
    */
  implicit object RawFileConverter extends JsonConverter[RawFile] {
    override def applyOpt(json: JObject): Option[RawFile] = {
      for {
        path <- (json \ "path").extractOpt[String]
      } yield new RawFile(path, getSpec(json))
    }
  }
}
