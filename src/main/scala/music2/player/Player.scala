package music2.player

import music2.output.Out
import music2.player.PlayableImplicits.Playable

abstract class Player[T](implicit out: Out) {

  private var relativeStep: Double = 0
  var speed: Double = 1

  def play(implicit p: Playable[T]): T = {
    val played = _play
    relativeStep += 1
    played
  }

  protected def _play(implicit p: Playable[T]): T

  protected def step = (relativeStep * speed) / out.sampleRate
}
