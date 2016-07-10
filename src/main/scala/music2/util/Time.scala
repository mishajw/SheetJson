package music2.util

import music2.BPM

object Time {


  abstract class Time[T <: Time[_]](private val value: Double) extends Ordered[Time[T]] {
    def cons(value: Double): T

    def +(t: Time[T]) = cons(value + t.value)

    def *(t: Time[T]) = cons(value * t.value)

    def /(t: Time[T]) = cons(value / t.value)

    def %(t: Time[T]) = cons(value % t.value)

    def incr = cons(value + 1)

    override def compareTo(that: Time[T]): BPM = (value - that.value).toInt

    override def compare(that: Time[T]) = value compare that.value

    def toDouble = value

    def toInt = toDouble.toInt
  }

  /**
    * Represents seconds
    * Used for calculating wave functions
    */
  case class Seconds(_value: Double) extends Time[Seconds](_value) {
    override def cons(value: Double): Seconds = new Seconds(value)
  }

  object Seconds {
    def apply(a: Absolute): Seconds =
      Seconds(a._value / Config.sampleRate)
    def apply(b: Bars): Seconds =
      Seconds(((Config.bpm / Config.beatsPerBar) / 60) * b._value)
  }

  /**
    * Represents amount of ticks called
    * Used for time-independent things, like `Looper`
    */
  case class Absolute(_value: Double) extends Time[Absolute](_value) {
    override def cons(value: Double): Absolute = new Absolute(value)
  }

  object Absolute {
    def apply(s: Seconds): Absolute =
      Absolute(s._value * Config.sampleRate)
    def apply(b: Bars): Absolute =
      Absolute(Seconds(b))
  }

  /**
    * Represents musical bars, set by a BPM
    * Used for timing how long things last, such as durations, spans, and looping lengths
    */
  case class Bars(_value: Double) extends Time[Bars](_value) {
    override def cons(value: Double): Bars = new Bars(value)
  }

  object Bars {
    def apply(s: Seconds): Bars =
      Bars(s._value / (Config.bpm.toDouble / Config.beatsPerBar.toDouble / 60.0))
    def apply(a: Absolute): Bars =
      Bars(Seconds(a))
  }
}
