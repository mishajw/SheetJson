package music2.management

import music2.output.{Out, SoundAndFileOut}
import music2.player.PlayableImplicits.Playable
import music2.player.Player

object Composer {
  def play[T](player: Player[T], seconds: Double, out: Out)(implicit p: Playable[T]): Unit = {
    val steps = (music2.sampleRate * seconds).toInt

    for (_ <- 0 until steps) {
      out.play(player.play)
    }

    Thread.sleep((seconds * 1000).toInt)
  }

  def play[T](player: Player[T], time: Long)(implicit p: Playable[T]): Unit = {
    val out = new SoundAndFileOut("out.pcm")

    out.start()
    play(player, time, out)
    out.stop()
  }
}
