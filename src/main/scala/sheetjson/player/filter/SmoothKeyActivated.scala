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
//    val sincePressed = Bars(absoluteStep - pressedAbsolute)
//    val sincePressedAbsolute = Absolute(sincePressed)

//    while (sincePressedAbsolute.toInt >= childPlays.length)
//      childPlays += child.play

//    val played = childPlays(sincePressedAbsolute.toInt)

    val fadeInScaledOpt = lastPressed
      .map(lastReleased.getOrElse(absoluteStep) - _)
      .map(Bars.apply)
      .map(_ / fadeInTime)
      .map(_.toDouble)

    val fadeOutScaledOpt = lastReleased
      .map(absoluteStep - _)
      .map(Bars.apply)
      .map(_ / fadeOutTime)
      .map(_.toDouble)

    val totalProgress =
      fadeInScaledOpt.map(Math.min(1, _)).getOrElse(0.0) -
      fadeOutScaledOpt.getOrElse(0.0)

    val playedOpt = lastPressed
      .map(absoluteStep - _)
      .map(_.toInt)
      .map(sincePressed => {
        while (sincePressed >= childPlays.length)
          childPlays += child.play

        childPlays(sincePressed)
      })
      .map(_ * fadeFunction(totalProgress))

    playedOpt match {
      case Some(played) if totalProgress >= 0 =>
        played
      case _ =>
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
