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
  trait FilterConverter extends JsonConverter {
    final def apply(json: JObject): Option[Player] = {
      val children: Seq[Option[Player]] = for {
        JObject(obj) <- json
        ("child", child: JObject) <- obj
      } yield JsonParser.parseJson(child)

      children.flatten.headOption match {
        case Some(child) => applyWithChild(child, json)
        case None => None
      }
    }

    protected def applyWithChild(child: Player, json: JObject): Option[Player]
  }

  /**
    * Convert to `Smoother`
    */
  object SmootherConverter extends FilterConverter {

    override val identifier: String = "smoother"

    override protected def applyWithChild(child: Player, json: JObject): Option[Player] = {
      json \ "smoothness" match {
        case JDouble(smoothness) => Some(new Smoother(child, smoothness, getSpec(json)))
        case _ => None
      }
    }
  }

  /**
    * Convert to `Randomizer`
    */
  object RandomizerConverter extends FilterConverter {
    override val identifier: String = "randomizer"

    override protected def applyWithChild(child: Player, json: JObject): Option[Player] = {
      json \ "randomness" match {
        case JDouble(randomness) => Some(new Randomizer(child, randomness, getSpec(json)))
        case _ => None
      }
    }
  }

  /**
    * Convert to `KeyActivated`
    */
  object KeyActivatedConverter extends FilterConverter {
    override val identifier: String = "key-activated"

    override protected def applyWithChild(child: Player, json: JObject): Option[Player] = {
      json \ "key" match {
        case JInt(key) => Some(new KeyActivated(key.toInt, child, getSpec(json)))
        case _ => None
      }
    }
  }

  /**
    * Convert to `Looper`
    */
  object LooperConverter extends FilterConverter {
    override val identifier: String = "looper"

    override protected def applyWithChild(child: Player, json: JObject): Option[Player] = {
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
  object ToggleConverter extends FilterConverter {
    override val identifier: String = "toggle"

    override protected def applyWithChild(child: Player, json: JObject): Option[Player] = for {
      JInt(key) <- Option(json \ "key")
    } yield new Toggle(key.toInt, child, getSpec(json))
  }
}
