package sheetjson.management.json.converters

import sheetjson.management.json.JsonParser
import sheetjson.player.Player
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
    final def apply(json: JObject): Try[T] = {
      val children: Seq[JObject] = for {
        JObject(obj) <- json
        ("child", child: JObject) <- obj
      } yield child

      children match {
        case Seq(child) => for {
          player <- JsonParser.parsePlayerJson(child)
          filter <- applyWithChild(player, json)
        } yield filter
      }
    }

    protected def applyWithChild(child: Player, json: JObject): Try[T]
  }

  /**
    * Convert to `Smoother`
    */
  implicit object SmootherConverter extends FilterConverter[Smoother] {
    override protected def applyWithChild(child: Player, json: JObject): Try[Smoother] = {
      json \ "smoothness" match {
        case JDouble(smoothness) => Success(new Smoother(child, smoothness, getSpec(json)))
        case _ => Failure(new JsonParsingException("Couldn't parse smoother", json))
      }
    }
  }

  /**
    * Convert to `Randomizer`
    */
  implicit object RandomizerConverter extends FilterConverter[Randomizer] {
    override protected def applyWithChild(child: Player, json: JObject): Try[Randomizer] = {
      json \ "randomness" match {
        case JDouble(randomness) => Success(new Randomizer(child, randomness, getSpec(json)))
        case _ => Failure(new JsonParsingException("Couldn't parse randomizer", json))
      }
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
    override protected def applyWithChild(child: Player, json: JObject): Try[Looper] = {
      Option(json \ "seconds") match {
        case Some(JDouble(bars)) =>
          Success(new Looper(Bars(bars), child, getSpec(json)))
        case _ => Failure(new JsonParsingException("Couldn't parse looper", json))
      }
    }
  }

  /**
    * Convert to `Toggle`
    */
  implicit object ToggleConverter extends FilterConverter[Toggle] {
    override protected def applyWithChild(child: Player, json: JObject): Try[Toggle] = json \ "key" match {
      case JInt(key) =>
        Success(new Toggle(key.toInt, child, getSpec(json)))
      case _ => Failure(new JsonParsingException("Couldn't parse toggle", json))
    }
  }
}

