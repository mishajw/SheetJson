package music2.util

import javax.sound.sampled.AudioFormat

import music2.{BPM, SampleRate}

/**
  * For documentation on parameters, see Config object accessors
  */
case class Config(sampleRate: SampleRate, bpm: BPM, beatsPerBar: Int)

object Config {
  /**
    * The default configuration. If no configuration is given, this is used
    */
  private val defaultConfig = Config(24000, 120, 4)

  /**
    * The current active configuration
    */
  private var config: Config = defaultConfig

  /**
    * Sample rate for audio format
    * @return
    */
  def sampleRate = config.sampleRate

  /**
    * Beats per minute
    */
  def bpm = config.bpm

  /**
    * How many beats are there per bar
    * If 4, the time signature is 4/4
    * If 3, the time signature is 3/4
    */
  def beatsPerBar: Int = config.beatsPerBar

  /**
    * Update the current configuration
    * @param newConfig the configuration to update to
    */
  def update(newConfig: Config) = config = newConfig

  /**
    * Get an audio format using parameters from the current configuration
    * @return
    */
  def format =
    new AudioFormat(sampleRate, 16, 1, true, false)
}
