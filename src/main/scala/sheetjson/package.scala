import org.json4s.JValue
import org.json4s.jackson.JsonMethods

import scala.util.Failure

/**
  * Created by misha on 14/06/16.
  */
package object sheetjson {
  type SampleRate = Int
  type Frequency = Double
  type BPM = Int

  def jsonFailure(str: String) = Failure(new JsonParsingException(str))
  def jsonFailure(str: String, json: JValue) = Failure(new JsonParsingException(str, json))

  class JsonParsingException(str: String) extends Throwable(str) {
    def this(str: String, json: JValue) { this(s"$str:\n${JsonMethods.pretty(json)}") }
  }
}
