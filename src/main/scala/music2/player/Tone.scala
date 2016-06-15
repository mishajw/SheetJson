package music2.player

import music2.Frequency

/**
  * Plays a single tone
  * @param frequency the frequency of the tone
  */
class Tone(val frequency: Frequency, _spec: PlayerSpec = PlayerSpec()) extends Player(_spec) {

  /**
    * Stores 2pi
    */
  val fullAngle = Math.PI * 2

  private lazy val waveLength = 1 / frequency

  protected def _play: Playable = {
    val progress = (step % waveLength) / waveLength
    val angle = progress * fullAngle

    Playable(Math.sin(angle))
  }
}
