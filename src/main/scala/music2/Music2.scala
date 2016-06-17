package music2

import com.typesafe.scalalogging.Logger
import music2.management.Composer
import music2.management.json.JsonParser
import music2.player.Player

object Music2 {

  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {
    val playerOpt = args match {
      case Array("--path", path) =>
        JsonParser parse path
      case Array("--raw", raw) =>
        JsonParser parseRaw raw
      case _ =>
        log.error("Usage: music2 [--path | --raw] [<file path> | <JSON string>]")
        return
    }

    playerOpt match {
      case Some(player) =>
        log.debug(s"Create players: ${Player.flatten(player)}")
        Composer play player
      case None =>
        log.error("JSON was malformed, or file not found")
    }
  }
}
