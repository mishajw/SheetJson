package music2.management

import music2.player.Player

import scala.io.Source

object JsonParser {

  /**
    * Parse JSON from a file
    * @param path path of the file
    * @tparam T the type of Player to return
    * @return an option of a player from the JSON file
    */
  def parse[T](path: String): Option[Player[T]] = {
    try {
      val f = Source.fromFile(path)
      val raw = f.mkString
      f.close()

      parseRaw(raw)
    } catch {
      // TODO: Catch only relevant exceptions
      case e: Exception => None
    }
  }

  /**
    * Parse a JSON string
    * @param json the JSON string
    * @tparam T the type of Player to return
    * @return an option of a player from the JSON string
    */
  def parseRaw[T](json: String): Option[Player[T]] = ???
}
