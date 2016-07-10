package sheetjson.management

import com.typesafe.scalalogging.Logger
import sheetjson.output.{Out, SoundAndFileOut}
import sheetjson.player.{EndPlayable, Player}

/**
  * Responsible for playing music from a Player object
  */
object Composer {

  private val log = Logger(getClass)

  /**
    * Play from a Player
 *
    * @param player the Player to play from
    * @param out the output
    */
  def play[T](player: Player, out: Out): Unit = {
    new KeyListener(player)

    log.debug(s"Start taking from $player and putting into $out")

    while (player.alive) {
      out.play(player.play)
    }

    log.debug("Stop playing")

    out.play(new EndPlayable())
  }

  /**
    * Play from a player, and create the output
 *
    * @param player the Player to play from
    */
  def play[T](player: Player): Unit = {
    val out = new SoundAndFileOut("out.pcm")
    log.debug(s"Make new out: $out")

    out.start()
    play(player, out)
    out.stop()
  }
}
