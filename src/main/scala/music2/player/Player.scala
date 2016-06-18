package music2.player

import music2.player.composite.CompositePlayer
import music2.player.filter.FilterPlayer
import music2.util.Time.sampleRate

/**
  * Represents something that "plays" music
  */
abstract class Player(val playerSpec: PlayerSpec) {

  /**
    * How many times play has been called on this `Player`
    */
  protected var absoluteStep: Int = 0

  /**
    * The speed of the `Player`
    */
  var speed: Double = playerSpec.speed getOrElse 1

  /**
    * How long a `Player` plays for
    */
  val lifeTime: Option[Double] = playerSpec.lifeTime

  /**
    * How loud a `Player` is
    */
  val volume: Double = playerSpec.volume getOrElse 0.5

  /**
    * @return the next value played, and handle other transformations
    */
  def play: Playable = {
    if (!alive) return Playable.default

    val played = _play * volume
    absoluteStep += 1
    played
  }

  /**
    * @return the next value played
    */
  protected def _play: Playable

  /**
    * @return the relative step, scaled by speed and sample rate
    */
  def step = fromAbsolute(absoluteStep)

  def fromAbsolute(abs: Int) = (abs * speed) / sampleRate

  def toAbsolute(x: Double): Int = ((x * sampleRate) / speed).toInt

  /**
    * @return whether the `Player` is alive
    */
  def alive: Boolean = lifeTime match {
    case Some(lt) => step <= lt && childrenAlive
    case None => true
  }

  def childrenAlive: Boolean = false
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
