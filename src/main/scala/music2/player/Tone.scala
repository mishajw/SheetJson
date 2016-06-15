package music2.player

import music2.{Frequency, LifeTime}
import music2.player.PlayableImplicits.Playable

/**
  * Plays a single tone
  * @param frequency the frequency of the tone
  */
class Tone(val frequency: Frequency, _lifeTime: LifeTime = None) extends Player[Int](_lifeTime) {

  /**
    * Stores 2pi
    */
  val fullAngle = Math.PI * 2

  private lazy val waveLength = 1 / frequency

  protected def _play(implicit p: Playable[Int]): Int = {
    val progress = (step % waveLength) / waveLength
    val angle = progress * fullAngle

    (Math.sin(angle) * 32767).asInstanceOf[Int]
  }
}
