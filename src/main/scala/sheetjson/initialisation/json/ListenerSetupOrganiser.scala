package sheetjson.initialisation.json

import sheetjson.initialisation.json.converters.listener.ListenerSetup
import sheetjson.input.KeyListener
import sheetjson.player.listener._
import sheetjson.util.Identifiable

object ListenerSetupOrganiser {
  def setup(player: Identifiable, keyListener: KeyListener): Unit ={
    player match {
      case listenerPlayer: ListenerPlayer =>
        listenerPlayer.listeners foreach (setup(_, listenerPlayer, keyListener))
      case _ =>
    }

    player.identifiableChildren
      .filter(_ != player)
      .foreach(setup(_, keyListener))
  }

  def setup(listener: Listener, listenerPlayer: ListenerPlayer, keyListener: KeyListener): Unit = {
    def convert[T <: Listener]
    (listener: T)
    (implicit ls: ListenerSetup[T]): Unit = listenerPlayer.spec.createdWith match {
      case Some(json) =>
        ls.setup(listener, json, keyListener)
      case None =>
    }

    listener match {
      case l: ActivatableListener => convert(l)
      case l: MultiActivatableListener => convert(l)
      case l: IncrementableListener => convert(l)
    }

  }
}
