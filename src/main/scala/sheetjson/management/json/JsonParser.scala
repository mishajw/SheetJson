package sheetjson.management.json

import java.io.FileNotFoundException

import com.typesafe.scalalogging.Logger
import sheetjson.management.json.converters.JsonConverter
import sheetjson.management.json.converters.composite._
import sheetjson.management.json.converters.filter._
import sheetjson.management.json.converters.origin._
import sheetjson.player.Player
import sheetjson.player.composite._
import sheetjson.player.filter._
import sheetjson.player.origin.{FadingNoise, RawFile, Tone}
import sheetjson.util.Config
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.jackson.JsonMethods
import sheetjson.JsonParsingException

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Parses JSON objects into Player objects
  */
object JsonParser {

  private val log = Logger(getClass)

  implicit val formats = DefaultFormats

  /**
    * Parse JSON from a file
 *
    * @param path path of the file
    * @return an option of a player from the JSON file
    */
  def parse(path: String): Try[Player] = {
    try {
      val f = Source.fromFile(path)
      val raw = f.mkString
      f.close()

      parseRaw(raw)
    } catch {
      case e: FileNotFoundException =>
        Failure(e)
    }
  }

  /**
    * Parse a JSON string
 *
    * @param jsonStr the JSON string
    * @return an option of a player from the JSON string
    */
  def parseRaw(jsonStr: String): Try[Player] = {
    JsonMethods.parseOpt(jsonStr) match {
      case Some(json: JObject) => parseAllJson(json)
      case _ =>
        Failure(new JsonParsingException("Provided JSON is not valid", JString(""))) // TODO: dutty hack
    }
  }

  /**
    * Parse a JSON object
 *
    * @param json the JSON object
    * @return an option of a player from the JSON object
    */
  def parseAllJson(json: JObject): Try[Player] = {
    Option(json \ "config") match {
      case Some(configJson: JObject) =>
        Config.update(configJson.extract[Config])
      case _ =>
    }

    // TODO: Use `Try` here
    json \ "players" match {
      case playerJson @ JObject(_) =>
        parsePlayerJson(playerJson)
      case _ =>
        Failure(new JsonParsingException("No players field in json", json))
    }
  }

  def parsePlayerJson(json: JValue): Try[Player] = for {
    obj <- Try(json.extract[JObject])
    t <- getType(obj)
    player <- getPlayer(t, obj)
  } yield player

  /**
    * Get the type string of a JSON object
    */
  private def getType(json: JObject): Try[String] = {
    json.obj collect {
      case ("type", JString(t)) => t
    } match {
      case t :: xs => Success(t)
      case _ => Failure(new JsonParsingException("Couldn't get type for json", json))
    }
  }

  def getPlayer(id: String, json: JObject): Try[Player] = {
    def convert[T <: Player]
               (json: JObject)
               (implicit jc: JsonConverter[T]): Try[T] = jc(json)

    log.info(s"Getting player with ID $id")

    id match {
      case "tone" => convert[Tone](json)
      case "fading-noise" => convert[FadingNoise](json)
      case "key-activated" => convert[KeyActivated](json)
      case "looper" => convert[Looper](json)
      case "randomizer" => convert[Randomizer](json)
      case "smoother" => convert[Smoother](json)
      case "toggle" => convert[Toggle](json)
      case "combiner" => convert[Combiner](json)
      case "keyboard" => convert[Keyboard](json)
      case "keyboard-scale" => convert[KeyboardScale](json)
      case "riff" => convert[Riff](json)
      case "switcher" => convert[Switcher](json)
      case "rawfile" => convert[RawFile](json)
      case other => for {
        preset <- Config.getPreset(other)
        filledIn = fillInPreset(preset, json)
        player <- parsePlayerJson(filledIn)
      } yield player
    }
  }

  def fillInPreset(preset: JObject, json: JObject): JObject = {
    val toReplace: Map[String, JValue] = (for {
      JObject(obj) <- json
      (name, value) <- obj
      if name != "type"
    } yield (name, value)).toMap

    (preset transformField {
      case JField(name, JString(value))
        if toReplace.keys.toSeq contains value =>
          JField(name, toReplace(value))
    }).asInstanceOf[JObject]
  }
}
