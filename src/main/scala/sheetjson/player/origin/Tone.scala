package sheetjson.player.origin

import sheetjson.Frequency
import sheetjson.player.{Playable, PlayerSpec, WaveFunction}
import sheetjson.util.Time.Seconds

/**
  * Plays a single tone
  *
  * @param frequency the frequency of the tone
  */
class Tone( val frequency: Frequency,
            val waveFunction: WaveFunction = WaveFunction get "sine",
            _spec: PlayerSpec) extends OriginPlayer(_spec) {

  private lazy val waveLength = 1 / frequency

  protected def _play: Playable = {
    val progress = (step % Seconds(waveLength)) / Seconds(waveLength)
    Playable(waveFunction(progress.toDouble))
  }

  override def toString: String = s"Tone($frequency)"
}
