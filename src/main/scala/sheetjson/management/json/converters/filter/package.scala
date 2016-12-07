package sheetjson.management.json.converters

import sheetjson.jsonFailure
import sheetjson.management.json.JsonParser
import sheetjson.player.Player
import sheetjson.player.origin.Tone.waveFunctions
import sheetjson.player.filter._
import sheetjson.util.Time.Bars
import org.json4s.JObject
import org.json4s.JsonAST.{JDouble, JInt}
import sheetjson.JsonParsingException

import scala.util.{Failure, Success, Try}

package object filter {

  /**
    * Convert to `FilterPlayer` types
    */
  trait FilterConverter[T <: FilterPlayer] extends JsonConverter[T] {
    override final def apply(json: JObject): Try[T] = {
      val children: Seq[JObject] = for {
        JObject(obj) <- json
        ("child", child: JObject) <- obj
      } yield child

      children match {
        case Seq(child) => for {
          player <- JsonParser.parsePlayerJson(child)
          filter <- applyWithChild(player, json)
        } yield filter
        case Seq() => jsonFailure("")
      }
    }

    protected def applyWithChild(child: Player, json: JObject): Try[T] = applyWithChildOpt(child, json) match {
      case Some(t) => Success(t)
      case None => jsonFailure(s"Couldn't parse JSON for Filter player ${getClass}", json)
    }

    protected def applyWithChildOpt(child: Player, json: JObject): Option[T] = None
  }

  /**
    * Convert to `Smoother`
    */
  implicit object SmootherConverter extends FilterConverter[Smoother] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[Smoother] = {
      for {
        smoothness <- (json \ "smoothness").extractOpt[Double]
      } yield new Smoother(child, smoothness, getSpec(json))
    }
  }

  /**
    * Convert to `Randomizer`
    */
  implicit object RandomizerConverter extends FilterConverter[Randomizer] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[Randomizer] = {
      for {
        randomness <- (json \ "randomness").extractOpt[Double]
      } yield new Randomizer(child, randomness, getSpec(json))
    }
  }

  /**
    * Convert to `KeyActivated`
    */
  implicit object KeyActivatedConverter extends FilterConverter[KeyActivated] {
    override protected def applyWithChild(child: Player, json: JObject): Try[KeyActivated] = {
      json \ "key" match {
        case JInt(key) => Success(new KeyActivated(key.toInt, child, getSpec(json)))
        case _ => Failure(new JsonParsingException("Couldn't parse key activated", json))
      }
    }
  }

  /**
    * Convert to `Looper`
    */
  implicit object LooperConverter extends FilterConverter[Looper] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[Looper] = {
      for {
        bars <- (json \ "seconds").extractOpt[Double]
      } yield new Looper(Bars(bars), child, getSpec(json))
    }
  }

  /**
    * Convert to `Toggle`
    */
  implicit object ToggleConverter extends FilterConverter[Toggle] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[Toggle] = {
      for {
        key <- (json \ "key").extractOpt[Int]
      } yield new Toggle(key.toInt, child, getSpec(json))
    }
  }

  /**
    * Convert to `VolumeFunction`
    */
  implicit object VolumeFunctionConverter extends FilterConverter[VolumeFunction] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[VolumeFunction] = {
      for {
        frequency <- (json \ "frequency").extractOpt[Double]
        waveFunction <- (json \ "waveFunction").extractOpt[String]
        wave <- waveFunctions get waveFunction
      } yield new VolumeFunction(wave, Bars(frequency), child, getSpec(json))
    }
  }
}

