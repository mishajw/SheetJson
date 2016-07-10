package music2.management.json.converters

import music2.management.json.JsonParser
import music2.player.{Player, PlayerSpec}
import org.json4s.JsonAST.JArray
import org.json4s.{DefaultFormats, JObject}

trait JsonConverter[T <: Player] {
  implicit val formats = DefaultFormats

  /**
    * @param json the JSON object to convert
    * @return the converted `Player` object
    */
  def apply(json: JObject): Option[T]

  /**
    * Get the `PlayerSpec` from a JSON object
    */
  protected def getSpec(json: JObject): PlayerSpec = {
    json.extract[PlayerSpec]
  }

  /**
    * Get a list of `Player` components from a JSON object
    */
  protected def getComponents(json: JObject): Seq[Player] = for {
    JObject(obj) <- json
    ("components", JArray(components)) <- obj
    jsonComponent @ JObject(_) <- components
    component <- JsonParser parsePlayerJson jsonComponent
  } yield component
}
