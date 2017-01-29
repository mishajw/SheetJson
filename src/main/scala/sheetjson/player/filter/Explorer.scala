package sheetjson.player.filter

import sheetjson.player.listener.{IncrementableListener, Listener, ListenerPlayer}
import sheetjson.player.{Playable, Player, PlayerSpec}
import sheetjson.util.Time.{Absolute, Seconds}

import scala.collection.mutable.ArrayBuffer

class Explorer(val increment: Seconds,
               _child: Player,
               _spec: PlayerSpec)
    extends FilterPlayer(_child, _spec) with ListenerPlayer {

  var index: Absolute = Absolute(0)

  val childPlays = ArrayBuffer[Playable]()

  override protected def _play: Playable = {
    if (index.toInt < 0)
      index = Absolute(0)

    while (index.toInt >= childPlays.length)
      childPlays += child.play

    val played = childPlays(index.toInt)

    index = index + Absolute(1)

    played
  }

  override val listeners: Seq[Listener] = Seq(new IncrementableListener {
    override def next(): Unit = {
      index = index + Absolute(increment)
    }

    override def previous(): Unit = {
      index = index - Absolute(increment)
    }
  })
}
