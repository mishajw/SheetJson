package music2.management.json.converters

import music2.management.KeyListener.KeyCode
import music2.management.json.JsonParser
import music2.player.Player
import music2.player.composite.Riff.{PlayerDescription, PlayerDuration, PlayerSpan}
import music2.player.composite._
import music2.util.Time.Bars
import org.json4s.JsonAST.{JArray, JDouble, JInt}
import org.json4s.{JObject, JValue}

package object composite {

  /**
    * Convert to composite `Player`s
 *
    * @tparam T
    */
  trait CompositeConverter[T <: CompositePlayer[_], V] extends JsonConverter[T] {
    override def apply(json: JObject): Option[T] = {
      val cs: Seq[V] = (for {
        JObject(obj) <- json
        ("components", JArray(components)) <- obj
        component <- components
      } yield convertWrapped(component)).flatten

      applyWithComponents(cs, json)
    }

    protected def convertWrapped(json: JValue): Option[V]

    protected def applyWithComponents(cs: Seq[V], json: JObject): Option[T]
  }

  /**
    * Convert to a `Combiner`
    */
  implicit object CombinerConverter extends CompositeConverter[Combiner, Player] {
    override protected def convertWrapped(json: JValue): Option[Player] = json match {
      case j: JObject => JsonParser parsePlayerJson j
      case _ => None
    }

    override protected def applyWithComponents(cs: Seq[Player], json: JObject): Option[Combiner] =
      Some(new Combiner( cs, getSpec(json)))
  }

  /**
    * Convert to `Riff`
    */
  implicit object RiffConverter extends CompositeConverter[Riff, PlayerDescription] {
    override protected def convertWrapped(json: JValue): Option[PlayerDescription] = json match {
      case JArray((jsonComponent: JObject) :: description) =>
        (JsonParser parsePlayerJson jsonComponent, description) match {
          case (Some(player), List(JDouble(start), JDouble(end))) =>
            Some(PlayerSpan(player, Bars(start), Bars(end)))
          case (Some(player), List(JDouble(duration))) =>
            Some(PlayerDuration(player, Bars(duration)))
          case _ => None
        }
      case _ => None
    }

    override protected def applyWithComponents(cs: Seq[PlayerDescription], json: JObject): Option[Riff] =
      Some(new Riff(cs, getSpec(json)))
  }

  /**
    * Convert to `Keyboard`
    */
  implicit object KeyboardConverter extends CompositeConverter[Keyboard, (Player, KeyCode)] {
    override protected def convertWrapped(json: JValue): Option[(Player, KeyCode)] = json match {
      case JArray(List(child: JObject, JInt(keyCode))) =>
        (JsonParser parsePlayerJson child) map ((_, keyCode.toInt))
    }

    override protected def applyWithComponents(cs: Seq[(Player, KeyCode)], json: JObject): Option[Keyboard] =
      Some(new Keyboard(cs, getSpec(json)))
  }

  /**
    * Convert to `Switcher`
    */
  implicit object SwitcherConverter extends CompositeConverter[Switcher, (KeyCode, Player)] {
    override protected def convertWrapped(json: JValue): Option[(KeyCode, Player)] = json match {
      case JArray(List(JInt(keyCode), child: JObject)) =>
        (JsonParser parsePlayerJson child) map ((keyCode.toInt, _))
      case _ => None
    }

    override protected def applyWithComponents(cs: Seq[(KeyCode, Player)], json: JObject): Option[Switcher] = {
      Some(new Switcher(cs, getSpec(json)))
    }
  }
}
