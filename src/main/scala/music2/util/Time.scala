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
    * Represents seconds
    * Used for calculating wave functions
    */
  case class Seconds(value: Double)

  /**
    * Represents amount of ticks called
    * Used for time-independent things, like `Looper`
    */
  case class Absolute(value: Int)

  /**
    * Represents musical bars, set by a BPM
    * Used for timing how long things last, such as durations, spans, and looping lengths
    */
  case class Bars(value: Double)
}
