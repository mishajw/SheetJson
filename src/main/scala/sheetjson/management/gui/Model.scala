package sheetjson.management.gui

import java.util.Observable
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import sheetjson.player.{Playable, Player}

import scala.collection.convert.decorateAsScala._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Model extends Observable {

  private type Map[T, V] = mutable.LinkedHashMap[T, V]
  private type Queue[T] = ConcurrentLinkedQueue[T]

  private val readings = new Map[Player, (ArrayBuffer[Playable], Int)]()

  private val readingsKept = 1000

  def addReading(player: Player, reading: Playable): Unit = {
    readings get player match {
      case Some((a, i)) =>
        a(i) = reading
        readings put (player, (a, (i + 1) % readingsKept))
      case None =>
        val newQueue = ArrayBuffer.fill(readingsKept) { Playable.default }
        newQueue(0) = reading
        readings put (player, (newQueue, 1))
    }

    changed()
  }

  private def changed() = {
    setChanged()
    notifyObservers()
  }

  def allReadings: Traversable[(Player, Traversable[Playable])] = {
    readings
      .toSeq
      .sortBy { case (p, (a, i)) => p.identifier }
      .map { case (p, (a, i)) => (p, {
        a.drop(i) ++ a.take(i)
      }) }
  }
}
