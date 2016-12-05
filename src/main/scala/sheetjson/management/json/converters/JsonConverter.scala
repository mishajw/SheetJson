package sheetjson.management.json.converters

import sheetjson.jsonFailure
import sheetjson.management.json.JsonParser
import sheetjson.player.{Player, PlayerSpec}
import org.json4s.JsonAST.JArray
import org.json4s.{DefaultFormats, JObject}

import scala.util.{Failure, Success, Try}

trait JsonConverter[T <: Player] {
  implicit val formats = DefaultFormats

  /**
    * @param json the JSON object to convert
    * @return the converted `Player` object
    */
  def apply(json: JObject): Try[T] = {
    applyOpt(json) match {
      case Some(t) => Success(t)
      case None => jsonFailure(s"Couldn't parse JSON for ${getClass}", json)
    }
  }

  /**
    * Convert and return and optional T
    */
  def applyOpt(json: JObject): Option[T] = None

  /**
    * Get the `PlayerSpec` from a JSON object
    */
  protected def getSpec(json: JObject): PlayerSpec = {
    json.extract[PlayerSpec]
  }

  /**
    * Get a list of `Player` components from a JSON object
    */
  protected def getComponents(json: JObject): Try[Seq[Player]] = {
    val parseAttempts: Seq[Try[Player]] = for {
      JObject(obj) <- json
      ("components", JArray(components)) <- obj
      jsonComponent @ JObject(_) <- components
      component = JsonParser parsePlayerJson jsonComponent
    } yield component

    parseAttempts collect { case Failure(e) => e } match {
      case e :: _ => Failure(e)
      case _ => Success(parseAttempts collect { case Success(p) => p})
    }
  }
}
