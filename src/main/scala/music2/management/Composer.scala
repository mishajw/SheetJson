package music2.management

import music2.output.{Out, SoundAndFileOut}
import music2.player.Player

/**
  * Responsible for playing music from a Player object
  */
object Composer {

  /**
    * Play from a Player
    * @param player the Player to play from
    * @param seconds how long to play for
    * @param out the output
    */
  def play[T](player: Player, seconds: Double, out: Out): Unit = {
    val steps = (music2.sampleRate * seconds).toInt

    for (_ <- 0 until steps) {
      out.play(player.play)
    }

    Thread.sleep((seconds * 1000).toInt)
  }

  /**
    * Play from a player, and create the output
    * @param player the Player to play from
    * @param time how long to play for
    */
  def play[T](player: Player, time: Long): Unit = {
    val out = new SoundAndFileOut("out.pcm")

    out.start()
    play(player, time, out)
    out.stop()
  }
}
