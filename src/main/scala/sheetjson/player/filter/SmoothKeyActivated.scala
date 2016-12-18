package sheetjson.player.filter

import sheetjson.player._
import sheetjson.player.activatable.SingleKeyInteractivePlayer
import sheetjson.player.activatable.SingleKeyInteractivePlayer.SingleKeyInteractiveSpec
import sheetjson.util.Time.{Absolute, Bars}

import scala.collection.mutable.ArrayBuffer

class SmoothKeyActivated(val inFunction: WaveFunction,
                         val outFunction: WaveFunction,
                         val fadeInTime: Bars,
                         val fadeOutTime: Bars,
                         _child: Player,
                         _spec: PlayerSpec,
                         override val interactiveSpec: SingleKeyInteractiveSpec)
    extends FilterPlayer(_child, _spec) with SingleKeyInteractivePlayer {

  val childPlays: ArrayBuffer[Playable] = ArrayBuffer()

  var lastPressed: Option[Absolute] = None
  var lastReleased: Option[Absolute] = None

  override protected def _play: Playable = lastPressed match {
    case Some(absolute) => getChildPlayed(absolute)
    case None => Playable.default
  }

  def getChildPlayed(pressedAbsolute: Absolute): Playable = {
    val sincePressed = Bars(absoluteStep - pressedAbsolute)
    val sincePressedAbsolute = Absolute(sincePressed)

    while (sincePressedAbsolute.toInt >= childPlays.length)
      childPlays += child.play

    val played = childPlays(sincePressedAbsolute.toInt)

    lastReleased match {
      case None =>
        if (sincePressed < fadeInTime) {
          played * inFunction((sincePressed / fadeInTime).toDouble)
        } else {
          played
        }
      case Some(releasedAbsolute) =>
        val sinceRelease = Bars(absoluteStep - releasedAbsolute)
        if (sinceRelease < fadeOutTime) {
          played * outFunction((sinceRelease / fadeOutTime).toDouble)
        } else {
          lastPressed = None
          lastReleased = None
          Playable.default
        }
    }

  }

  override def activate(): Unit = {
    if (lastPressed.isEmpty)
      lastPressed = Some(absoluteStep)
  }

  override def deactivate(): Unit = {
    lastReleased = Some(absoluteStep)
  }

}
