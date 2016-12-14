package sheetjson.management.json.converters

import org.json4s.JsonAST.{JArray, JDouble, JInt}
import org.json4s.{JObject, JValue}
import sheetjson.jsonFailure
import sheetjson.management.KeyListener.KeyCode
import sheetjson.management.json.JsonParser
import sheetjson.player.Player
import sheetjson.player.composite.Riff.{PlayerDescription, PlayerDuration, PlayerSpan}
import sheetjson.player.composite._
import sheetjson.util.Time.Bars

import scala.util.{Failure, Success, Try}

package object composite {

  /**
    * Convert to composite `Player`s
    *
    * @tparam T
    */
  trait CompositeConverter[T <: CompositePlayer[_], V] extends JsonConverter[T] {
    override def apply(json: JObject): Try[T] = {
      val csAttempts: Seq[Try[V]] = (json \ "components").extractOpt[JArray] match {
        case Some(JArray(components)) => components map convertWrapped
        case None => Seq()
      }

      csAttempts collect { case Failure(e) => e } match {
        case e :: _ => Failure(e)
        case _ =>
          applyWithComponents(
            csAttempts collect { case Success(v) => v }, json)
      }
    }

    protected def convertWrapped(json: JValue): Try[V]

    protected def applyWithComponents(cs: Seq[V], json: JObject): Try[T] = applyWithComponentsOpt(cs, json) match {
      case Some(t) => Success(t)
      case None => jsonFailure(s"Couldn't parse JSON for Composite player $getClass", json)
    }

    protected def applyWithComponentsOpt(cs: Seq[V], json: JObject): Option[T] = None
  }

  /**
    * Convert to a `Combiner`
    */
  implicit object CombinerConverter extends CompositeConverter[Combiner, Player] {
    override protected def convertWrapped(json: JValue): Try[Player] =
      JsonParser parsePlayerJson json

    override protected def applyWithComponents(cs: Seq[Player], json: JObject): Try[Combiner] =
      Success(new Combiner(cs, getSpec(json)))
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
          case _ => jsonFailure("Couldn't parse to riff", json)
        }
      case _ =>
        jsonFailure("Couldn't parse to riff", json)
    }

    override protected def applyWithComponents(cs: Seq[PlayerDescription], json: JObject): Try[Riff] =
      Success(new Riff(cs, getSpec(json)))
  }

  /**
    * Convert to `Switcher`
    */
  implicit object SwitcherConverter extends CompositeConverter[Switcher, (KeyCode, Player)] {
    override protected def convertWrapped(json: JValue): Try[(KeyCode, Player)] = json match {
      case JArray(List(JInt(keyCode), child: JValue)) =>
        (JsonParser parsePlayerJson child) map ((keyCode.toInt, _))
      case _ => jsonFailure("Couldn't parse to Switcher child", json)
    }

    override protected def applyWithComponents(cs: Seq[(KeyCode, Player)], json: JObject): Try[Switcher] = {
      Success(new Switcher(cs, getSpec(json)))
    }
  }

  /**
    * Convert to `Scroller`
    */
  implicit object ScrollerConverter extends CompositeConverter[Scroller, Player] {
    override protected def convertWrapped(json: JValue): Try[Player] =
      JsonParser parsePlayerJson json

    override protected def applyWithComponentsOpt(cs: Seq[Player], json: JObject): Option[Scroller] = {
      for {
        nextKey <- (json \ "next_key").extractOpt[Int]
        previousKey <- (json \ "previous_key").extractOpt[Int]
      } yield new Scroller(nextKey, previousKey, cs, getSpec(json))
    }
  }
}
