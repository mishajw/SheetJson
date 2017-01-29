package sheetjson.player.filter

import sheetjson.player.listener.{IncrementableListener, Listener, ListenerPlayer}
import sheetjson.player.{Playable, Player, PlayerSpec}
import sheetjson.util.PlayerCache
import sheetjson.util.Time.{Absolute, Seconds}

class Clipper(initialEnd: Seconds,
              initialIncrement: Seconds,
              incrementMultiplier: Double,
              _child: Player,
              _spec: PlayerSpec) extends FilterPlayer(_child, _spec) with ListenerPlayer {

  val childCache = new PlayerCache(child)

  var childStep = Absolute(0)

  var start = Absolute(0)

  var end = Absolute(initialEnd)

  var increment = Absolute(initialIncrement)

  override protected def _play: Playable = {
    if (start.toInt < 0)
      start = Absolute(0)

    if (end.toInt < 0)
      end = Absolute(0)

    if (childStep < start || childStep > end)
      childStep = start

    val played = childCache.play(childStep)

    childStep = childStep.incr

    played
  }

  override val listeners: Seq[Listener] = Seq(
    new IncrementableListener {
      override val name: Option[String] = Some("start")

      override def next(): Unit ={
        start = start + increment
        childStep = start
      }

      override def previous(): Unit ={
        start = start - increment
        childStep = start
      }
    },

    new IncrementableListener {
      override val name: Option[String] = Some("end")

      override def next(): Unit = end = end + increment

      override def previous(): Unit = end = end - increment
    },

    new IncrementableListener {
      override val name: Option[String] = Some("increment")

      override def next(): Unit = increment = increment * Absolute(incrementMultiplier)

      override def previous(): Unit = increment = increment / Absolute(incrementMultiplier)
    }
  )
}
