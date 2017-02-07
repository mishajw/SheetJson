package sheetjson.player

import sheetjson.player.composite.CompositePlayer
import sheetjson.player.filter.FilterPlayer
import sheetjson.util.Messagable
import sheetjson.util.Time.{Absolute, Bars, Seconds}

import scala.collection.mutable.ArrayBuffer

/**
  * Represents something that "plays" music
  */
abstract class Player(val spec: PlayerSpec) extends Messagable {

  /**
    * How many times play has been called on this `Player`
    */
  protected var absoluteStep: Absolute = Absolute(0)

  /**
    * The speed of the `Player`
    */
  var speed: Double = spec.speed getOrElse 1

  /**
    * How long a `Player` plays for
    */
  val lifeTime: Option[Bars] = spec.lifeTime

  /**
    * The default volume level
    */
  protected val defaultVolume: Double = 1

  /**
    * How loud a `Player` is
    */
  def volume: Double = spec.volume getOrElse defaultVolume

  /**
    * A history of `Playable`s played by this player
    */
  val history: ArrayBuffer[Playable] = ArrayBuffer()

  /**
    * @return the next value played, and handle other transformations
    */
  def play: Playable = {
    if (!alive) return Playable.default

    val played = _play * volume
    absoluteStep = absoluteStep.incr

    history += played

    played
  }

  /**
    * @return the next value played
    */
  protected def _play: Playable

  def step: Seconds = Seconds(absoluteStep)

  /**
    * @return whether the `Player` is alive
    */
  def alive: Boolean = lifeTime match {
    case Some(lt) => Bars(step) <= lt && childrenAlive
    case None => true
  }

  def childrenAlive: Boolean = false

  def displayParameters: Seq[Object] = Seq()

  override def toString: String = {
    (spec.name, spec.`type`) match {
      case (Some(n), _) => n
      case (None, Some(t)) => t
      case _ => super.toString
    }
  }
}

object Player {
  def flatten(p: Player): Seq[Player] = p match {
    case p: CompositePlayer[_] =>
      p +: p.components.flatMap(flatten)
    case p: FilterPlayer =>
      p +: flatten(p.child)
    case p: Player =>
      Seq(p)
  }
}
