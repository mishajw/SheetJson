package sheetjson.initialisation.json.converters

import org.json4s.JsonAST.JArray
import org.json4s.{DefaultFormats, Formats, JObject}
import sheetjson.initialisation.json.JsonParser
import sheetjson.jsonFailure
import sheetjson.player.{Player, PlayerSpec}

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
      case None => jsonFailure(s"Couldn't parse JSON for $getClass", json)
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
      .copy(createdWith = Some(json))
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

  protected def extractTry[ExtractType]
                          (json: JObject, name: String)
                          (implicit formats: Formats, mf: scala.reflect.Manifest[ExtractType]): Try[ExtractType] = {

    (json \ name).extractOpt[ExtractType] match {
      case Some(et) => Success(et)
      case None => jsonFailure(s"Couldn't extract $name from JSON", json)
    }
  }
}
