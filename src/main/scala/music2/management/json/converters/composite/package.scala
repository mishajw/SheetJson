package music2.management.json.converters

import music2.management.json.JsonParser
import music2.player.Player
import music2.player.composite.Riff.{PlayerDescription, PlayerDuration, PlayerSpan}
import music2.player.composite.{Combiner, Riff}
import org.json4s.JsonAST.{JArray, JDouble}
import org.json4s.{JObject, JValue}

package object composite {

  /**
    * Convert to composite `Player`s
    * @tparam T
    */
  trait CompositeConverter[T] extends JsonConverter {
    override def apply(json: JObject): Option[Player] = {
      val cs: Seq[T] = (for {
        JObject(obj) <- json
        ("components", JArray(components)) <- obj
        component <- components
      } yield convertWrapped(component)).flatten

      applyWithComponents(cs, json)
    }

    protected def convertWrapped(json: JValue): Option[T]

    protected def applyWithComponents(cs: Seq[T], json: JObject): Option[Player]
  }

  /**
    * Convert to a `Combiner`
    */
  object CombinerConverter extends CompositeConverter[Player] {
    override val identifier: String = "combiner"

    override protected def convertWrapped(json: JValue): Option[Player] = json match {
      case j: JObject => JsonParser parseJson j
      case _ => None
    }

    override protected def applyWithComponents(cs: Seq[Player], json: JObject): Option[Player] =
      Some(new Combiner( cs, getSpec(json)))
  }

  /**
    * Convert to `Riff`
    */
  object RiffConverter extends CompositeConverter[PlayerDescription] {
    override val identifier: String = "riff"

    override protected def convertWrapped(json: JValue): Option[PlayerDescription] = json match {
      case JArray((jsonComponent: JObject) :: description) =>
        (JsonParser parseJson jsonComponent, description) match {
          case (Some(player), List(JDouble(start), JDouble(end))) =>
            Some(PlayerSpan(player, start, end))
          case (Some(player), List(JDouble(duration))) =>
            Some(PlayerDuration(player, duration))
          case _ => None
        }
      case _ => None
    }

    override protected def applyWithComponents(cs: Seq[PlayerDescription], json: JObject): Option[Player] =
      Some(new Riff(cs, getSpec(json)))
  }
}
