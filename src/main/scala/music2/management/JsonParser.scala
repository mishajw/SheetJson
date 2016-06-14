package music2.management

import music2.player.Player

import scala.io.Source

object JsonParser {
  def parse[T](path: String): Option[Player[T]] = {
    try {
      val f = Source.fromFile(path)
      val raw = f.mkString
      f.close()

      Some(parseRaw(raw))
    } catch {
      // TODO: Catch only relevant exceptions
      case e: Exception => None
    }
  }

  def parseRaw[T](json: String): Player[T] = ???
}
