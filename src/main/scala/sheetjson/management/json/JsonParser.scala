package sheetjson.management.json

import java.io.FileNotFoundException

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

import scala.Option
import scala.io.Source

/**
  * Parses JSON objects into Player objects
  */
object JsonParser {

  implicit val formats = DefaultFormats

  /**
    * Parse JSON from a file
 *
    * @param path path of the file
    * @return an option of a player from the JSON file
    */
  def parse(path: String): Option[Player] = {
    try {
      val f = Source.fromFile(path)
      val raw = f.mkString
      f.close()

      parseRaw(raw)
    } catch {
      case e: FileNotFoundException => None
    }
  }

  /**
    * Parse a JSON string
 *
    * @param jsonStr the JSON string
    * @return an option of a player from the JSON string
    */
  def parseRaw(jsonStr: String): Option[Player] = {
    JsonMethods.parseOpt(jsonStr) match {
      case Some(json: JObject) => parseAllJson(json)
      case _ =>
        System.err.println("Provided JSON is not valid")
        None
    }
  }

  /**
    * Parse a JSON object
 *
    * @param json the JSON object
    * @return an option of a player from the JSON object
    */
  def parseAllJson(json: JObject): Option[Player] = {
    Option(json \ "config") match {
      case Some(configJson: JObject) =>
        Config.update(configJson.extract[Config])
      case _ =>
    }

    for {
      JObject(playerJson) <- Option(json \ "players")
      player <- parsePlayerJson(JObject(playerJson))
    } yield player
  }

  def parsePlayerJson(json: JObject): Option[Player] = for {
    t <- getType(json)
    player <- getPlayer(t, json)
  } yield player

  /**
    * Get the type string of a JSON object
    */
  private def getType(json: JObject): Option[String] = {
    json.obj collect {
      case ("type", JString(t)) => t
    } headOption
  }

  def getPlayer(id: String, json: JObject): Option[Player] = {
    def convert[T <: Player]
               (json: JObject)
               (implicit jc: JsonConverter[T]): Option[T] = jc(json)

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
