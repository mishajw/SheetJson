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
            val waveFunction: WaveFunction = waveFunctions("sine"),
            _spec: PlayerSpec) extends OriginPlayer(_spec) {

  private lazy val waveLength = 1 / frequency

  protected def _play: Playable = {
    val progress = (step % Seconds(waveLength)) / Seconds(waveLength)
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

  val waveFunctions: Map[String, WaveFunction] = Map(
    "sine" -> { x: Double => Math.sin(x * fullAngle) },
    "cosine" -> { x: Double => Math.cos(x * fullAngle) },
    "id" -> { x: Double => x },
    "binary" -> { x: Double => if (x < 0.5) -1 else 1 }
  )
}
