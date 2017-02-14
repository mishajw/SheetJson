package sheetjson.gui

import java.util.Observable

import sheetjson.player.{Playable, Player}
import sheetjson.util.RootPlayerAssignable

class Controller extends Observable with RootPlayerAssignable {
  def changed() = {
    setChanged()
    notifyObservers()
  }

  def allReadings: Traversable[(Player, Traversable[Playable])] = rootPlayerOpt match {
    case Some(rootPlayer) =>
      rootPlayer.flattenPlayers map (p => (p, p.history))
    case None => Seq()
  }
}
