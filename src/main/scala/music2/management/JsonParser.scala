package music2.management

import java.io.FileNotFoundException

import music2.player.Player

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
  def parse(path: String): Option[Player[Int]] = {
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
    * @param json the JSON string
    * @return an option of a player from the JSON string
    */
  def parseRaw(json: String): Option[Player[Int]] = ???
}
