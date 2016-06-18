package music2.player.filter

import music2.player.{Playable, Player, PlayerSpec}
import music2.util.Time.{Absolute, Bars}

import scala.collection.mutable.ArrayBuffer

class Looper(val bars: Bars,
             _child: Player,
             _spec: PlayerSpec) extends FilterPlayer(_child, _spec) {

  val past = ArrayBuffer[ArrayBuffer[Playable]]()

  override protected def _play: Playable = {
    val currentStep = absoluteStep.value % Absolute(bars).value

    while (past.size < currentStep + 1) {
      past += ArrayBuffer()
    }

    past(currentStep.toInt) += child.play

    past(currentStep.toInt) combine
  }
}

