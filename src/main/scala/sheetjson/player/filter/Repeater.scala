package sheetjson.player.filter

import sheetjson.player.{Playable, Player, PlayerSpec}
import sheetjson.util.Time.{Absolute, Bars}

import scala.collection.mutable.ArrayBuffer

class Repeater(val length: Bars, _child: Player, _spec: PlayerSpec) extends FilterPlayer(_child, _spec) {

  private val childValues: ArrayBuffer[Playable] = ArrayBuffer()

  override protected def _play: Playable = {
    val stepInRepeat = absoluteStep % Absolute(length)

    while (childValues.length <= stepInRepeat.toInt)
      childValues += child.play

    childValues(stepInRepeat.toInt)
  }
}
