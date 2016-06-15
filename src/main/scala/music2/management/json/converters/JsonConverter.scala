package music2.management.json.converters

import music2.management.json.JsonParser
import music2.player.{Player, PlayerSpec}
import org.json4s.JsonAST.JArray
import org.json4s.{DefaultFormats, JObject}

trait JsonConverter {
  implicit val formats = DefaultFormats

  val identifier: String

  def apply(json: JObject): Option[Player]

  protected def getSpec(json: JObject): PlayerSpec = {
    json.extract[PlayerSpec]
  }

  protected def getComponents(json: JObject): Seq[Player] = for {
    JObject(obj) <- json
    ("components", JArray(components)) <- obj
    jsonComponent @ JObject(_) <- components
    component <- JsonParser parseJson jsonComponent
  } yield component

}
