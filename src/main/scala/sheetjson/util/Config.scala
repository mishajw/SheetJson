package sheetjson.util

import javax.sound.sampled.AudioFormat

import sheetjson.{BPM, SampleRate}
import org.json4s.JObject
import org.json4s.JsonAST.JValue

case class Preset(name: String, constructor: JObject)

/**
  * For documentation on parameters, see Config object accessors
  */
case class Config(sampleRate: SampleRate, bpm: BPM, beatsPerBar: Int, presets: Seq[Preset])

object Config {
  /**
    * The default configuration. If no configuration is given, this is used
    */
  private val defaultConfig = Config(24000, 120, 4, Seq())

  /**
    * The current active configuration
    */
  private var config: Config = defaultConfig

  /**
    * Sample rate for audio format
 *
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
    * Get a preset from the configuration
    * A preset is a template for creating Players
 *
    * @param name name of the preset
    * @return the preset corresponding to that name
    */
  def getPreset(name: String): Option[JObject] =
    config.presets
      .filter(_.name == name)
      .map(_.constructor)
      .headOption

  /**
    * Update the current configuration
 *
    * @param newConfig the configuration to update to
    */
  def update(newConfig: Config) = config = newConfig

  /**
    * Get an audio format using parameters from the current configuration
 *
    * @return
    */
  def format =
    new AudioFormat(sampleRate, 16, 1, true, false)
}
