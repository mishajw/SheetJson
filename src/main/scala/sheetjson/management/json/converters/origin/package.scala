package sheetjson.management.json.converters

import sheetjson.player.composite.KeyboardScale
import sheetjson.player.origin.Tone._
import sheetjson.player.origin.{FadingNoise, RawFile, Tone}
import sheetjson.util.Frequencies.FrequencyOf
import sheetjson.util.Time.Bars
import sheetjson.util.{Notes, Scales}
import org.json4s.JObject
import org.json4s.JsonAST.{JDouble, JString}

package object origin {

  /**
    * Convert to `Tone`
    */
  implicit object ToneConverter extends JsonConverter[Tone] {

    case class JTone(note: Double, waveFunction: String = "sine")

    override def apply(json: JObject): Option[Tone] = {
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

  /**
    * Convert to `FadingNoise`
    */
  implicit object FadingNoiseConverter extends JsonConverter[FadingNoise] {
    override def apply(json: JObject): Option[FadingNoise] = {
      for {
        JDouble(length) <- Option(json \ "length")
      } yield new FadingNoise(Bars(length), getSpec(json))
    }
  }

  /**
    * Convert to `Keyboard`
    */
  implicit object KeyboardScaleConverter extends JsonConverter[KeyboardScale] {
    /**
      * @param json the JSON object to convert
      * @return the converted `Player` object
      */
    override def apply(json: JObject): Option[KeyboardScale] = json match {
      case jsonObj: JObject =>
        for {
          JString(scale) <- Option(json \ "scale")
          JString(key) <- Option(json \ "key")
          note <- Notes relativeNoteFor key
          scale <- Scales get(note, scale)
        } yield new KeyboardScale(scale, getSpec(jsonObj))
      case _ => None
    }
  }

  /**
    * Convert to `RawFile`
    */
  implicit object RawFileConverter extends JsonConverter[RawFile] {
    override def apply(json: JObject): Option[RawFile] = {
      val rfs: Seq[RawFile] = for {
        JObject(obj) <- json
        ("path", JString(path)) <- obj
      } yield new RawFile(path, getSpec(json))

      rfs.headOption
    }
  }
}
