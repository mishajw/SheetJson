package music2.player

import Playable._

class Playable(private val value: Double) {
  def toBytes: Seq[Byte] = {
    val i: Int = (value * (rangeInt16 / rangePlayable)).toInt

    Seq(i, i >> 8).map(_.toByte)
  }
}

object Playable {

  def apply(value: Double) = new Playable(value)

  implicit class PlayableCollection(val col: Traversable[Playable]) extends AnyVal {
    def average: Playable = Playable(col.map(_.value).sum / col.size)
  }

  val default = new Playable(0)

  private val maxInt16: Int = (Math.pow(2, 15) - 1).toInt
  private val minInt16: Int = (-Math.pow(2, 15)).toInt
  private val rangeInt16: Int = maxInt16 - minInt16

  private val maxPlayable: Double = 1
  private val minPlayable: Double = -1
  private val rangePlayable: Double = maxPlayable - minPlayable
}
