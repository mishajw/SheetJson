package sheetjson

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.management.json.JsonParser
import sheetjson.management.{Composer, Identifiable}
import sheetjson.output.SoundAndFileOut
import sheetjson.player.Player
import sheetjson.util.Config

import scala.util.{Failure, Success}

object SheetJson {

  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {

    val keyListener = new KeyListener()
    Config.keyListener = Some(keyListener)

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
        val composer = new Composer(player, keyListener)
        val out = new SoundAndFileOut("out.pcm")
        out.start()

        log.debug(s"Create players: ${Player.flatten(player)}")
        Identifiable.propagateParents(player)
        composer play out

        out.stop()
      case Failure(e) =>
        log.error("Got error from parsing JSON", e)
    }
  }
}
