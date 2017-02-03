package sheetjson.management

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.output.Out
import sheetjson.player.listener.{Listener, ListenerPlayer}
import sheetjson.player.{EndPlayable, Player}
import sheetjson.util.Messagable.Message

/**
  * Responsible for playing music from a Player object
  */
class Composer(rootPlayer: Player) {

  private val log = Logger(getClass)

  /**
    * Play from a Player
    * @param out the output
    */
  def play(out: Out): Unit = {
    log.debug(s"Start taking from $rootPlayer and putting into $out")

    while (rootPlayer.alive) {
      out.play(rootPlayer.play)
    }

    log.debug("Stop playing")

    out.play(new EndPlayable())
  }

  def sendMessage(message: Message): Unit = {
    rootPlayer.receive(message)
  }
}
