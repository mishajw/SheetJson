import org.json4s.JValue

/**
  * Created by misha on 14/06/16.
  */
package object sheetjson {
  type SampleRate = Int
  type Frequency = Double
  type BPM = Int

  class JsonParsingException(str: String) extends Throwable(str) {
    def this(str: String, json: JValue) { this(s"$str: $json") }
  }
}
