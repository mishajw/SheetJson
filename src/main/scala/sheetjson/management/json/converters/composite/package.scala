package sheetjson.management.json.converters

import sheetjson.management.KeyListener.KeyCode
import sheetjson.management.json.JsonParser
import sheetjson.player.Player
import sheetjson.player.composite.Riff.{PlayerDescription, PlayerDuration, PlayerSpan}
import sheetjson.player.composite._
import sheetjson.util.Time.Bars
import org.json4s.JsonAST.{JArray, JDouble, JField, JInt}
import org.json4s.{JObject, JValue}
import sheetjson.JsonParsingException

import scala.util.{Failure, Success, Try}

package object composite {

  /**
    * Convert to composite `Player`s
    *
    * @tparam T
    */
  trait CompositeConverter[T <: CompositePlayer[_], V] extends JsonConverter[T] {
    override def apply(json: JObject): Try[T] = {
      val csAttempts: Seq[Try[V]] = for {
        JObject(obj) <- json
        ("components", JArray(components)) <- obj
        component <- components
      } yield convertWrapped(component)

      csAttempts collect { case Failure(e) => e } match {
        case e :: _ => Failure(e)
        case _ =>
          applyWithComponents(
            csAttempts collect { case Success(v) => v }, json)
      }
    }

    protected def convertWrapped(json: JValue): Try[V]

    protected def applyWithComponents(cs: Seq[V], json: JObject): Try[T]
  }

  /**
    * Convert to a `Combiner`
    */
  implicit object CombinerConverter extends CompositeConverter[Combiner, Player] {
    override protected def convertWrapped(json: JValue): Try[Player] = json match {
      case j: JObject => JsonParser parsePlayerJson j
      case _ => Failure(new JsonParsingException("Child wasn't an JObject", json))
    }

    override protected def applyWithComponents(cs: Seq[Player], json: JObject): Try[Combiner] =
      Success(new Combiner( cs, getSpec(json)))
  }

  /**
    * Convert to `Riff`
    */
  implicit object RiffConverter extends CompositeConverter[Riff, PlayerDescription] {
    override protected def convertWrapped(json: JValue): Try[PlayerDescription] = json match {
      case JArray((jsonComponent: JObject) :: description) =>
        (JsonParser parsePlayerJson jsonComponent, description) match {
          case (Success(player), List(JDouble(start), JDouble(end))) =>
            Success(PlayerSpan(player, Bars(start), Bars(end)))
          case (Success(player), List(JDouble(duration))) =>
            Success(PlayerDuration(player, Bars(duration)))
          case (Failure(e), _) => Failure(e)
          case _ => Failure(new JsonParsingException("Couldn't parse to riff", json))
        }
      case _ =>
        Failure(new JsonParsingException("Couldn't parse to riff", json))
    }

    override protected def applyWithComponents(cs: Seq[PlayerDescription], json: JObject): Try[Riff] =
      Success(new Riff(cs, getSpec(json)))
  }

  /**
    * Convert to `Keyboard`
    */
  implicit object KeyboardConverter extends CompositeConverter[Keyboard, (Player, KeyCode)] {
    override protected def convertWrapped(json: JValue): Try[(Player, KeyCode)] = json match {
      case JArray(List(child: JObject, JInt(keyCode))) =>
        (JsonParser parsePlayerJson child) map ((_, keyCode.toInt))
    }

    override protected def applyWithComponents(cs: Seq[(Player, KeyCode)], json: JObject): Try[Keyboard] =
      Success(new Keyboard(cs, getSpec(json)))
  }

  /**
    * Convert to `Switcher`
    */
  implicit object SwitcherConverter extends CompositeConverter[Switcher, (KeyCode, Player)] {
    override protected def convertWrapped(json: JValue): Try[(KeyCode, Player)] = json match {
      case JArray(List(JInt(keyCode), child: JObject)) =>
        (JsonParser parsePlayerJson child) map ((keyCode.toInt, _))
      case _ => Failure(new JsonParsingException("Couldn't parse to Switcher child", json))
    }

    override protected def applyWithComponents(cs: Seq[(KeyCode, Player)], json: JObject): Try[Switcher] = {
      Success(new Switcher(cs, getSpec(json)))
    }
  }
}
