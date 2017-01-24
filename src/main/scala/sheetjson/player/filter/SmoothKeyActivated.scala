package sheetjson.player.filter

import sheetjson.player._
import sheetjson.player.activatable.SingleKeyInteractivePlayer
import sheetjson.player.activatable.SingleKeyInteractivePlayer.SingleKeyInteractiveSpec
import sheetjson.util.Time.{Absolute, Bars}

import scala.collection.mutable.ArrayBuffer

class SmoothKeyActivated(val fadeFunction: WaveFunction,
                         val fadeInTime: Bars,
                         val fadeOutTime: Bars,
                         _child: Player,
                         _spec: PlayerSpec,
                         override val interactiveSpec: SingleKeyInteractiveSpec)
    extends FilterPlayer(_child, _spec) with SingleKeyInteractivePlayer {

  val childPlays: ArrayBuffer[Playable] = ArrayBuffer()

  var activationAmount: Double = 0

  var lastActiveOpt: Option[Absolute] = None

  override protected def _play: Playable = {
    if (isActive) {
      activationAmount += 1 / Absolute(fadeInTime).toDouble
    } else {
      activationAmount -= 1 / Absolute(fadeOutTime).toDouble
    }

    activationAmount = Math.max(0, Math.min(1, activationAmount))

    lastActiveOpt match {
      case Some(lastActive) if activationAmount > 0 =>
        val indexSinceStart = (absoluteStep - lastActive).toInt

        while (indexSinceStart >= childPlays.length)
          childPlays += child.play

        childPlays(indexSinceStart) * fadeFunction(activationAmount)
      case Some(_) =>
        lastActiveOpt = None
        Playable.default
      case None =>
        lastActiveOpt = Some(absoluteStep)
        _play
    }
  }

  override protected def _activate(): Unit = {}

  override protected def _deactivate(): Unit = {}
}
