package sheetjson.management

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.output.Out
import sheetjson.player.listener.{Listener, ListenerPlayer}
import sheetjson.player.{EndPlayable, Player}

/**
  * Responsible for playing music from a Player object
  */
class Composer(rootPlayer: Player, keyListener: KeyListener) {

  keyListener registerMessageSender sendMessage

  private val log = Logger(getClass)

  val listenerMap: Map[String, Listener] =
    Player.flatten(rootPlayer)
      .flatMap {
        case p: ListenerPlayer =>
          p.listeners.map(l => (l.identifier, l))
        case _ => Seq()
      }
      .toMap

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

  def sendMessage(identifier: String, message: String): Unit = {
    listenerMap(identifier).receive(message)
  }
}

object Composer {
  type MessageSender = (String, String) => Unit
}
