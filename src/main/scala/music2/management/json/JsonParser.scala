package music2.management.json

import java.io.FileNotFoundException

import music2.management.json.converters.JsonConverter
import music2.management.json.converters.origin._
import music2.management.json.converters.composite._
import music2.management.json.converters.filter._
import music2.player.Player
import music2.player.composite._
import music2.player.filter._
import music2.player.origin.{FadingNoise, RawFile, Tone}
import org.json4s._
import org.json4s.jackson.JsonMethods

import scala.io.Source

/**
  * Parses JSON objects into Player objects
  */
object JsonParser {

  /**
    * Parse JSON from a file
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
    * @param jsonStr the JSON string
    * @return an option of a player from the JSON string
    */
  def parseRaw(jsonStr: String): Option[Player] = {
    JsonMethods.parseOpt(jsonStr) match {
      case Some(json: JObject) => parseJson(json)
      case _ =>
        System.err.println("Provided JSON is not valid")
        None
    }
  }

  /**
    * Parse a JSON object
    * @param json the JSON object
    * @return an option of a player from the JSON object
    */
  def parseJson(json: JObject): Option[Player] = for {
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
    }
  }
}
