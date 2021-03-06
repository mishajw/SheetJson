package sheetjson.initialisation.json.converters

import org.json4s.JObject
import sheetjson.initialisation.json.JsonParser
import sheetjson.jsonFailure
import sheetjson.player.filter._
import sheetjson.player.{Player, WaveFunction}
import sheetjson.util.Time.{Bars, Seconds}

import scala.util.{Success, Try}

package object filter {

  /**
    * Convert to `FilterPlayer` types
    */
  trait FilterConverter[T <: FilterPlayer] extends JsonConverter[T] {
    override final def apply(json: JObject): Try[T] = {
      for {
        childJson <- extractTry[JObject](json, "child")
        child <- JsonParser.parsePlayerJson(childJson)
        player <- applyWithChild(child, json)
      } yield player
    }

    protected def applyWithChild(child: Player, json: JObject): Try[T] = applyWithChildOpt(child, json) match {
      case Some(t) => Success(t)
      case None => jsonFailure(s"Couldn't parse JSON for Filter player $getClass", json)
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
    * Convert to `VolumeFunction`
    */
  implicit object VolumeFunctionConverter extends FilterConverter[VolumeFunction] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[VolumeFunction] = {
      for {
        frequency <- (json \ "frequency").extractOpt[Double]
        waveFunction <- (json \ "wave_function").extractOpt[String]
        wave <- WaveFunction getOpt waveFunction
      } yield new VolumeFunction(wave, Bars(frequency), child, getSpec(json))
    }
  }

  /**
    * Convert to `KeyActivated`
    */
  implicit object KeyActivatedConverter extends FilterConverter[KeyActivated] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[KeyActivated] = {
      val fadeFunctionName = (json \ "fade_function").extractOrElse("fade")
      val fadeInTime = (json \ "fade_in_time").extractOrElse(0.1)
      val fadeOutTime = (json \ "fade_out_time").extractOrElse(0.1)

      for {
        fadeFunction <- WaveFunction getOpt fadeFunctionName
      } yield {
        new KeyActivated(
          fadeFunction,
          Bars(fadeInTime),
          Bars(fadeOutTime),
          child,
          getSpec(json))
      }
    }
  }

  /**
    * Convert to `Repeater`
    */
  implicit object RepeaterConverter extends FilterConverter[Repeater] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[Repeater] = for {
      length <- (json \ "length").extractOpt[Double]
    } yield new Repeater(Bars(length), child, getSpec(json))
  }

  /**
    * Convert to `Explorer`
    */
  implicit object ExplorerConverter extends FilterConverter[Explorer] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[Explorer] = for {
      increment <- (json \ "increment").extractOpt[Double]
    } yield new Explorer(Seconds(increment), child, getSpec(json))
  }

  /**
   * Convert to `Clipper`
   */
  implicit object ClipperConverter extends FilterConverter[Clipper] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[Clipper] = {
      Some(new Clipper(child, getSpec(json)))
    }
  }
}

