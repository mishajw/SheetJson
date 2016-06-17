package music2.management.json.converters.composite

import music2.management.json.converters.JsonConverter
import music2.player.Player
import music2.player.composite.Keyboard
import music2.player.util.{Notes, Scales}
import org.json4s.JObject
import org.json4s.JsonAST.JString

/**
  * Created by misha on 17/06/16.
  */
object KeyboardScaleConverter extends JsonConverter {
  override val identifier: String = "keyboard-scale"

  /**
    * @param json the JSON object to convert
    * @return the converted `Player` object
    */
  override def apply(json: JObject): Option[Player] = json match {
    case jsonObj: JObject =>
      for {
        JString(scale) <- Option(json \ "scale")
        JString(key) <- Option(json \ "key")
        note <- Notes noteFor key
        scale <- Scales get(note, scale)
      } yield new Keyboard(scale, getSpec(jsonObj))
    case _ => None
  }
}
