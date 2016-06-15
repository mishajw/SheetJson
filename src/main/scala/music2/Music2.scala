package music2

import music2.management.Composer
import music2.management.json.JsonParser

object Music2 {
  def main(args: Array[String]): Unit = {
    val playerOpt = args match {
      case Array("--path", path) =>
        JsonParser parse path
      case Array("--raw", raw) =>
        JsonParser parseRaw raw
      case _ =>
        System.err.println("Usage: music2 [--path | --raw] [<file path> | <JSON string>]")
        return
    }

    playerOpt match {
      case Some(player) =>
        Composer play (player, 10)
      case None =>
        System.err.println("JSON was malformed, or file not found")
    }
  }
}
