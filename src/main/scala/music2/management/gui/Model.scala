package music2.management.gui

import java.util.Observable

import scala.collection.convert.decorateAsScala._
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import music2.player.{Playable, Player}

object Model extends Observable {

  private type Map[T, V] = ConcurrentHashMap[T, V]
  private type Queue[T] = ConcurrentLinkedQueue[T]

  private val readings = new Map[Player, Queue[Playable]]().asScala

  private val readingsKept = 1000

  def addReading(player: Player, reading: Playable): Unit = {
    readings get player match {
      case Some(queue) =>
        queue add reading

        // If the queue is too large, poll it
        while (queue.size() > readingsKept)
          queue.poll()
      case None =>
        val newQueue = new Queue[Playable]()
        newQueue add reading
        readings put (player, newQueue)
    }

    changed()
  }

  private def changed() = {
    setChanged()
    notifyObservers()
  }

  def allReadings: Traversable[(Player, Traversable[Playable])] = {
    readings map { case (p, rs) => (p, rs.asScala) }
  }
}
