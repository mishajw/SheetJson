package sheetjson.management.json.converters

import org.json4s.JsonAST.{JArray, JDouble}
import org.json4s.{JObject, JValue}
import sheetjson.jsonFailure
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
  implicit object SwitcherConverter extends CompositeConverter[Switcher, Player] {
    override protected def convertWrapped(json: JValue): Try[Player] =
      JsonParser parsePlayerJson json

    override protected def applyWithComponentsOpt(cs: Seq[Player], json: JObject): Option[Switcher] = {
      Some(new Switcher(cs, getSpec(json)))
    }
  }

  /**
    * Convert to `Scroller`
    */
  implicit object ScrollerConverter extends CompositeConverter[Scroller, Player] {
    override protected def convertWrapped(json: JValue): Try[Player] =
      JsonParser parsePlayerJson json

    override protected def applyWithComponentsOpt(cs: Seq[Player], json: JObject): Option[Scroller] = {
      Some(new Scroller(cs, getSpec(json)))
    }
  }
}
