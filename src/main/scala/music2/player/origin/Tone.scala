package music2.player.origin

import music2.Frequency
import music2.player.origin.Tone._
import music2.player.{Playable, PlayerSpec}
import music2.util.Time.Seconds

/**
  * Plays a single tone
  * @param frequency the frequency of the tone
  */
class Tone( val frequency: Frequency,
            val waveFunction: WaveFunction = sine,
            _spec: PlayerSpec = PlayerSpec()) extends OriginPlayer(_spec) {

  private lazy val waveLength = 1 / frequency

  protected def _play: Playable = {
    val progress = (step % Seconds(waveLength)) / Seconds(waveLength)
    println(progress)
    Playable(waveFunction(progress.toDouble))
  }

  override def toString: String = s"Tone($frequency)"
}

object Tone {

  /**
    * Stores 2pi
    */
  val fullAngle = Math.PI * 2

  /**
    * Define a type for a wave function (typically sine)
    * Takes a value 0 to 1 inclusively, although this isn't enforced
    */
  type WaveFunction = Double => Double

  val sine: WaveFunction =
    x => Math.sin(x * fullAngle)

  val cosine: WaveFunction =
    x => Math.cos(x * fullAngle)

  val id: WaveFunction = identity
}
