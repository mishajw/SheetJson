package sheetjson.management.gui

import java.util.Observable
import java.util.concurrent.ConcurrentLinkedQueue

import sheetjson.player.{Playable, Player}
import sheetjson.util.RootPlayerAssignable

import scala.collection.mutable

class Model extends Observable with RootPlayerAssignable {

  private type Map[T, V] = mutable.LinkedHashMap[T, V]
  private type Queue[T] = ConcurrentLinkedQueue[T]

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
