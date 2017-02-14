package sheetjson.player.filter

import sheetjson.player.listener.{IncrementableListener, Listener, ListenerPlayer}
import sheetjson.player.{Playable, Player, PlayerCache, PlayerSpec}
import sheetjson.util.Time.{Absolute, Bars, Seconds}

/**
  * A player to extract and repeat a certain part of the child
  */
class Clipper(_child: Player,
              _spec: PlayerSpec) extends FilterPlayer(_child, _spec) with ListenerPlayer {

  val childCache = new PlayerCache(child)

  var clipStep = Absolute(0)

  var clipStart = Seconds(0)

  var clipLength = Bars(1)

  var increment: Double = 1

  override protected def _play: Playable = {
    if (clipStart.toDouble < 0)
      clipStart = Seconds(0)

    if (clipLength.toInt < 0)
      clipLength = Bars(0)

    if (Bars(clipStep) > clipLength)
      clipStep = Absolute(0)

    val played = childCache.play(Absolute(clipStart) + clipStep)

    clipStep = clipStep.incr

    played
  }

  // TODO: Remove .toString
  override def displayParameters: Seq[Object] = Seq(clipStart, clipLength, increment.toString)

  override val listeners: Seq[Listener] = Seq(
    new IncrementableListener {
      override val name: Option[String] = Some("start")

      override def next(): Unit = {
        clipStart = clipStart + Seconds(increment)
      }

      override def previous(): Unit = {
        clipStart = clipStart - Seconds(increment)
      }
    },

    new IncrementableListener {
      override val name: Option[String] = Some("length")

      override def next(): Unit = clipLength = clipLength + Bars(increment)

      override def previous(): Unit = clipLength = clipLength - Bars(increment)
    },

    new IncrementableListener {
      override val name: Option[String] = Some("increment")

      override def next(): Unit = increment = increment * 2

      override def previous(): Unit = increment = increment / 2
    }
  )
}
