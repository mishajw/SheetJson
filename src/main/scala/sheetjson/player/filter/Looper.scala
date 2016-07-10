package sheetjson.player.filter

import sheetjson.player.{Playable, Player, PlayerSpec}
import sheetjson.util.Time.{Absolute, Bars}

import scala.collection.mutable.ArrayBuffer

class Looper(val bars: Bars,
             _child: Player,
             _spec: PlayerSpec) extends FilterPlayer(_child, _spec) {

  val past = ArrayBuffer[ArrayBuffer[Playable]]()

  override protected def _play: Playable = {
    val currentStep = absoluteStep % Absolute(bars)

    while (Absolute(past.size) < currentStep.incr) {
      past += ArrayBuffer()
    }

    past(currentStep.toInt) += child.play

    past(currentStep.toInt) combine
  }
}

