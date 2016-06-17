package music2.management

import music2.output.{Out, SoundAndFileOut}
import music2.player.{EndPlayable, Player}

/**
  * Responsible for playing music from a Player object
  */
object Composer {

  /**
    * Play from a Player
    * @param player the Player to play from
    * @param out the output
    */
  def play[T](player: Player, out: Out): Unit = {
    new KeyListener(player)

    while (player.alive) {
      out.play(player.play)
    }

    out.play(new EndPlayable())
  }

  /**
    * Play from a player, and create the output
    * @param player the Player to play from
    */
  def play[T](player: Player): Unit = {
    val out = new SoundAndFileOut("out.pcm")

    out.start()
    play(player, out)
    out.stop()
  }
}
