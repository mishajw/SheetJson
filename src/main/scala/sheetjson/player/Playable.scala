package sheetjson.player

import java.nio.{ByteBuffer, ByteOrder}

import sheetjson.player.Playable._

import scala.util.Try

class Playable(_value: Double) {

  private val value = trim(_value)

  def toBytes: Seq[Byte] = {
    val i: Int = (value * (rangeInt16 / rangePlayable)).toInt

    Seq(i, i >> 8).map(_.toByte)
  }

  def *(other: Double) = Playable(this.value * other)

  def toDouble = _value

  override def toString: String = s"Playable($value)"
}

object Playable {

  def apply(value: Double) = new Playable(value)

  def fromInt(value: Int) = {
    val scaled: Double = (value - minInt16).toDouble / (maxInt16 - minInt16).toDouble
    new Playable((scaled * 2) - 1)
  }

  def fromBytes(bs: Traversable[Byte]): Option[Playable] = {
    Try({
      val byteBuffer = ByteBuffer.wrap(bs.toArray).order(ByteOrder.LITTLE_ENDIAN)
      Playable fromInt byteBuffer.getShort().toInt
    }).toOption
  }

  implicit class PlayableCollection(val col: Traversable[Playable]) extends AnyVal {

    def values = col.map(_.value)

    def combine: Playable = Playable(values.sum)

    def max: Playable = Playable(values.max)

    def min: Playable = Playable(values.min)

    def normalised: Seq[Playable] = {
      val range = max.value - min.value

      values
        .map(v => ((v - min.value) / range) * (maxPlayable - minPlayable) + minPlayable)
        .map(Playable(_))
        .toSeq
    }
  }

  private def trim(value: Double): Double = {
    Math.max(minPlayable, Math.min(maxPlayable, value))
  }

  val default = new Playable(0)

  private val maxInt16: Int = (Math.pow(2, 15) - 1).toInt
  private val minInt16: Int = (-Math.pow(2, 15)).toInt
  private val rangeInt16: Int = maxInt16 - minInt16

  private val maxPlayable: Double = 1
  private val minPlayable: Double = -1
  private val rangePlayable: Double = maxPlayable - minPlayable
}
