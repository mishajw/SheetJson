package music2.util

object Time {

  /**
    * Represents seconds
    * Used for calculating wave functions
    * @param value the amount of seconds
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
