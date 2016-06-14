package music2.player

import music2.player.PlayableImplicits.Playable

/**
  * Represents something that "plays" music
  */
abstract class Player[T](val lifeTime: Option[Double]) {

  /**
    * How many times play has been called on this `Player`
    */
  private var absoluteStep: Double = 0

  /**
    * The speed of the `Player`
    */
  var speed: Double = 1

  /**
    * @return the next value played, and handle other transformations
    */
  def play(implicit p: Playable[T]): T = {
    if (!alive) return p.default

    val played = _play
    absoluteStep += 1
    played
  }

  /**
    * @return the next value played
    */
  protected def _play(implicit p: Playable[T]): T

  /**
    * @return the relative step, scaled by speed and sample rate
    */
  def step = (absoluteStep * speed) / music2.sampleRate

  def alive: Boolean = lifeTime match {
    case Some(lt) => step <= lt
    case None => false
  }
}
