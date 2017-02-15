package sheetjson.initialisation.json.converters

import org.json4s.{DefaultFormats, JObject}
import sheetjson.util.{Config, Preset}

package object misc {

  implicit val formats = DefaultFormats

  implicit object ConfigConverter {
    def apply(json: JObject): Config = Config(
      (json \ "sample_rate").extractOrElse(Config.defaultConfig.sampleRate),
      (json \ "bpm").extractOrElse(Config.defaultConfig.bpm),
      (json \ "beats_per_bar").extractOrElse(Config.defaultConfig.beatsPerBar),
      (json \ "presets").extract[Seq[Preset]],
      (json \ "display_amount").extractOrElse(Config.defaultConfig.displayAmount))
  }
}
