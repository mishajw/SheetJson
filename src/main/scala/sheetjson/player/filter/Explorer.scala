package sheetjson.player.filter

import sheetjson.player.activatable.IncrementalInteractivePlayer
import sheetjson.player.activatable.IncrementalInteractivePlayer.IncrementalInteractiveSpec
import sheetjson.player.{Playable, Player, PlayerSpec}
import sheetjson.util.Time.{Absolute, Seconds}

import scala.collection.mutable.ArrayBuffer

class Explorer(val increment: Seconds,
               _child: Player,
               _spec: PlayerSpec,
               override val interactiveSpec: IncrementalInteractiveSpec)
    extends FilterPlayer(_child, _spec) with IncrementalInteractivePlayer {

  // Must have an even amount of keys, so if there's `n` keys for going forwards,
  // there's `n` keys for going backwards
  assert(interactiveSpec.keys.size % 2 == 0)

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

  override def next(): Unit = {
    index = index + Absolute(increment)
  }

  override def previous(): Unit = {
    index = index - Absolute(increment)
  }
}
