package sheetjson

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.management.json.JsonParser
import sheetjson.management.{Composer, ListenerSetupOrganiser}
import sheetjson.output.SoundAndFileOut
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

        player.propagateParents()
        val keyListener = new KeyListener(player)
        ListenerSetupOrganiser.setup(player, keyListener)

        val composer = new Composer(player)
        val out = new SoundAndFileOut("out.pcm")
        out.start()
        composer play out
        out.stop()
      case Failure(e) =>
        log.error("Got error from parsing JSON", e)
    }
  }
}
