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

    def scale(value: Option[Absolute], max: Bars): Option[Double] = {
      value
        .map(absoluteStep - _)
        .map(Bars.apply)
        .map(_ / fadeInTime)
        .map(_.toDouble)
    }

    val fadeInScaledOpt = scale(lastPressed, fadeInTime)
    val fadeOutScaledOpt = scale(lastReleased, fadeOutTime)

    val totalProgress =
      fadeInScaledOpt.map(Math.min(1, _)).getOrElse(0.0) -
      fadeOutScaledOpt.getOrElse(0.0)

    println(s"$fadeInScaledOpt, $fadeOutScaledOpt -> $totalProgress")

    if (totalProgress >= 0) {
      played * fadeFunction(totalProgress)
    } else {
      lastPressed = None
      lastReleased = None
      Playable.default
    }
  }

  override def _activate(): Unit = {
    if (lastPressed.isEmpty)
      lastPressed = Some(absoluteStep)
  }

  override def _deactivate(): Unit = {
    lastReleased = Some(absoluteStep)
  }

}
