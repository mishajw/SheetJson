package music2.player.origin

import music2.player.{Playable, PlayerSpec}
import music2.util.Time.Bars

class FadingNoise(val length: Bars, _spec: PlayerSpec) extends OriginPlayer(_spec) {
  override protected def _play: Playable = {
    val progress = ((Bars(step) % length) / length).toDouble

    val random = (Math.random() * 2) - 1
    Playable(random * (1 - progress))
  }
}
