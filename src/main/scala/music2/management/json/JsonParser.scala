package music2.management.json

import java.io.FileNotFoundException

import music2.management.json.converters.composite._
import music2.management.json.converters.filter.{KeyActivatedConverter, LooperConverter, RandomizerConverter, SmootherConverter}
import music2.management.json.converters.origin.ToneConverter
import music2.player.Player
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
    func <- converters get t
    player <- func(json)
  } yield player

  /**
    * Get the type string of a JSON object
    */
  private def getType(json: JObject): Option[String] = {
    json.obj collect {
      case ("type", JString(t)) => t
    } headOption
  }

  /**
    * A map of type names to their converter objects
    */
  val converters =
    Seq(
      ToneConverter, CombinerConverter, RiffConverter,
      RandomizerConverter, SmootherConverter, KeyActivatedConverter,
      KeyboardScaleConverter, KeyboardConverter, SwitcherConverter,
      LooperConverter)
      .map(c => c.identifier -> c)
      .toMap
}
