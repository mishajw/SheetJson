package sheetjson

import com.typesafe.scalalogging.Logger
import sheetjson.management.Composer
import sheetjson.management.json.JsonParser
import sheetjson.player.Player

import scala.util.{Failure, Success}

object SheetJson {

  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {
    val playerOpt = args match {
      case Array("--path", path) =>
        JsonParser parse path
      case Array("--raw", raw) =>
        JsonParser parseRaw raw
      case _ =>
        log.error("Usage: sheet-json [--path | --raw] [<file path> | <JSON string>]")
        return
    }

    playerOpt match {
      case Success(player) =>
        log.debug(s"Create players: ${Player.flatten(player)}")
        Composer play player
      case Failure(e) =>
        log.error("Got error from parsing JSON", e)
    }
  }
}
