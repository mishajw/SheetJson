package music2.util

import music2.{BPM, SampleRate}

object Time {

  // TODO: Take these values from a config file

  /**
    * The sample rate used across the project
    */
  val sampleRate: SampleRate = 24000

  /**
    * The BPM (Beats Per Minute) used across the project
    */
  val bpm: BPM = 100

  /**
    * How many beats are there per bar
    * If 4, the time signature is 4/4
    * If 3, the time signature is 3/4
    */
  val beatsPerBar: Int = 4

  /**
    * Represents seconds
    * Used for calculating wave functions
    */
  case class Seconds(value: Double)

  object Seconds {
    def apply(a: Absolute): Seconds =
      Seconds(a.value / sampleRate)
    def apply(b: Bars): Seconds =
      Seconds(((bpm / beatsPerBar) / 60) * b.value)
  }

  /**
    * Represents amount of ticks called
    * Used for time-independent things, like `Looper`
    */
  case class Absolute(value: Int)

  object Absolute {
    def apply(s: Seconds): Absolute =
      Absolute((s.value * sampleRate).toInt)
    def apply(b: Bars): Absolute =
      Absolute(Seconds(b))
  }

  /**
    * Represents musical bars, set by a BPM
    * Used for timing how long things last, such as durations, spans, and looping lengths
    */
  case class Bars(value: Double)

  object Bars {
    def apply(s: Seconds): Bars =
      Bars(s.value / (bpm / beatsPerBar / 60))
    def apply(a: Absolute): Bars =
      Bars(Seconds(a))
  }
}
