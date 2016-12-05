package sheetjson.management.json.converters

import java.util.Optional

import sheetjson.player.composite.KeyboardScale
import sheetjson.player.origin.Tone._
import sheetjson.player.origin.{FadingNoise, RawFile, Tone}
import sheetjson.util.Frequencies.FrequencyOf
import sheetjson.util.Time.Bars
import sheetjson.util.{Notes, Scales}
import org.json4s.JObject
import org.json4s.JsonAST.{JDouble, JString}
import sheetjson.JsonParsingException

import scala.util.{Failure, Success, Try}

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
        waveFunction <- (transformed \ "waveFunction").extractOpt[String]
        wave <- waveFunctions get waveFunction
      } yield new Tone(note, wave, getSpec(json))
    }
  }

  /**
    * Convert to `FadingNoise`
    */
  implicit object FadingNoiseConverter extends JsonConverter[FadingNoise] {
    override def apply(json: JObject): Try[FadingNoise] = json \ "length" match {
      case JDouble(length) => Success(new FadingNoise(Bars(length), getSpec(json)))
      case _ => Failure(new JsonParsingException("Couldn't get length from json", json))
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
    override def apply(json: JObject): Try[KeyboardScale] = json match {
      case jsonObj: JObject =>
        val scaleOpt = for {
          JString(scale) <- Option(json \ "scale")
          JString(key) <- Option(json \ "key")
          note <- Notes relativeNoteFor key
          scale <- Scales get(note, scale)
        } yield scale

        scaleOpt match {
          case Some(scale) =>
            Success(new KeyboardScale(scale, getSpec(jsonObj)))
          case None =>
            Failure(new JsonParsingException("Couldn't get scale from json", json))
        }
      case _ =>
        Failure(new JsonParsingException("Json wasn't an object", json))
    }
  }

  /**
    * Convert to `RawFile`
    */
  implicit object RawFileConverter extends JsonConverter[RawFile] {
    override def apply(json: JObject): Try[RawFile] = {
      val rfs: Seq[RawFile] = for {
        JObject(obj) <- json
        ("path", JString(path)) <- obj
      } yield new RawFile(path, getSpec(json))

      rfs match {
        case Seq(rf) => Success(rf)
        case _ => Failure(new JsonParsingException("Couldn't get raw file from json", json))
      }
    }
  }
}
