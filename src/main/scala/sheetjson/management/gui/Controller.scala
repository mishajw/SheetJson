package sheetjson.management.gui

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
      Player.flatten(rootPlayer) map (p => (p, p.history))
    case None => Seq()
  }
}
