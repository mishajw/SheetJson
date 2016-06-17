package music2.player.filter

import music2.player.{Playable, Player, PlayerSpec}

import scala.collection.mutable.ArrayBuffer

class Looper(val seconds: Double,
             _child: Player,
             _spec: PlayerSpec) extends FilterPlayer(_child, _spec) {

  val past = ArrayBuffer[ArrayBuffer[Playable]]()

  override protected def _play: Playable = {
    val currentStep = absoluteStep % toAbsolute(seconds)

    while (past.size < currentStep + 1) {
      past += ArrayBuffer()
    }

    past(currentStep) += child.play

    past(currentStep) combine
  }
}
