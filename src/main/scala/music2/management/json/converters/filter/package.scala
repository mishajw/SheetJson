package music2.management.json.converters

import music2.management.json.JsonParser
import music2.player.Player
import music2.player.filter._
import music2.util.Time.Bars
import org.json4s.JObject
import org.json4s.JsonAST.{JDouble, JInt}

package object filter {

  /**
    * Convert to `FilterPlayer` types
    */
  trait FilterConverter[T <: FilterPlayer] extends JsonConverter[T] {
    final def apply(json: JObject): Option[T] = {
      val children: Seq[Option[Player]] = for {
        JObject(obj) <- json
        ("child", child: JObject) <- obj
      } yield JsonParser.parsePlayerJson(child)

      children.flatten.headOption match {
        case Some(child) => applyWithChild(child, json)
        case None => None
      }
    }

    protected def applyWithChild(child: Player, json: JObject): Option[T]
  }

  /**
    * Convert to `Smoother`
    */
  implicit object SmootherConverter extends FilterConverter[Smoother] {
    override protected def applyWithChild(child: Player, json: JObject): Option[Smoother] = {
      json \ "smoothness" match {
        case JDouble(smoothness) => Some(new Smoother(child, smoothness, getSpec(json)))
        case _ => None
      }
    }
  }

  /**
    * Convert to `Randomizer`
    */
  implicit object RandomizerConverter extends FilterConverter[Randomizer] {
    override protected def applyWithChild(child: Player, json: JObject): Option[Randomizer] = {
      json \ "randomness" match {
        case JDouble(randomness) => Some(new Randomizer(child, randomness, getSpec(json)))
        case _ => None
      }
    }
  }

  /**
    * Convert to `KeyActivated`
    */
  implicit object KeyActivatedConverter extends FilterConverter[KeyActivated] {
    override protected def applyWithChild(child: Player, json: JObject): Option[KeyActivated] = {
      json \ "key" match {
        case JInt(key) => Some(new KeyActivated(key.toInt, child, getSpec(json)))
        case _ => None
      }
    }
  }

  /**
    * Convert to `Looper`
    */
  implicit object LooperConverter extends FilterConverter[Looper] {
    override protected def applyWithChild(child: Player, json: JObject): Option[Looper] = {
      Option(json \ "seconds") match {
        case Some(JDouble(bars)) =>
          Some(new Looper(Bars(bars), child, getSpec(json)))
        case _ => None
      }
    }
  }

  /**
    * Convert to `Toggle`
    */
  implicit object ToggleConverter extends FilterConverter[Toggle] {
    override protected def applyWithChild(child: Player, json: JObject): Option[Toggle] = for {
      JInt(key) <- Option(json \ "key")
    } yield new Toggle(key.toInt, child, getSpec(json))
  }
}
