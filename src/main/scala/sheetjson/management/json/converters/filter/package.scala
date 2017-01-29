package sheetjson.management.json.converters

import org.json4s.JObject
import sheetjson.jsonFailure
import sheetjson.management.json.JsonParser
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
    * Convert to `KeyActivated`
    */
  implicit object KeyActivatedConverter extends FilterConverter[KeyActivated] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[KeyActivated] = {
      Some(new KeyActivated(child, getSpec(json)))
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
      Some(new Toggle(child, getSpec(json)))
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
    * Convert to `SmoothKeyActivated`
    */
  implicit object SmoothKeyActivatedConverter extends FilterConverter[SmoothKeyActivated] {
    override protected def applyWithChildOpt(child: Player, json: JObject): Option[SmoothKeyActivated] = {
      val fadeFunctionName = (json \ "fade_function").extractOrElse("fade")
      val fadeInTime = (json \ "fade_in_time").extractOrElse(0.25)
      val fadeOutTime = (json \ "fade_out_time").extractOrElse(0.25)

      for {
        fadeFunction <- WaveFunction getOpt fadeFunctionName
      } yield {
        new SmoothKeyActivated(
          fadeFunction,
          Bars(fadeInTime),
          Bars(fadeOutTime),
          child,
          getSpec(json))
      }
    }
  }

  /**
    * Toggle to `Repeater`
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
      val initialEnd = (json \ "initial_end").extractOrElse[Double](10)
      val initialIncrement = (json \ "initial_increment").extractOrElse[Double](2)
      val incrementMultiplier = (json \ "increment_multiplier").extractOrElse[Double](2)

      Some(new Clipper(Seconds(initialEnd), Seconds(initialIncrement), incrementMultiplier, child, getSpec(json)))
    }
  }
}

