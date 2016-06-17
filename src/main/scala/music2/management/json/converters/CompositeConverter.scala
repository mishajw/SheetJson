package music2.management.json.converters

import music2.player.Player
import music2.player.composite.CompositePlayer
import org.json4s.{JObject, JValue}
import org.json4s.JsonAST.JArray

trait CompositeConverter[T] extends JsonConverter {
  override def apply(json: JObject): Option[Player] = {
    val cs: Seq[T] = (for {
      JObject(obj) <- json
      ("components", JArray(components)) <- obj
      component <- components
    } yield convertWrapped(component)).flatten

    applyWithComponents(cs, json)
  }

  protected def convertWrapped(json: JValue): Option[T]

  protected def applyWithComponents(cs: Seq[T], json: JObject): Option[Player]
}
