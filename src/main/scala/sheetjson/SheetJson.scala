package sheetjson

import java.util.concurrent.ScheduledThreadPoolExecutor

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.management.gui.Model
import sheetjson.management.json.JsonParser
import sheetjson.management.{Composer, ListenerSetupOrganiser, PlayerReloader}
import sheetjson.output.SoundAndFileOut
import sheetjson.player.Player
import sheetjson.util.Config
import sheetjson.util.Time.{Absolute, Bars, Seconds}

import scala.util.{Failure, Success}

object SheetJson {

  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {

    val (playerOpt, originPathOpt) = args match {
      case Array("--path", path) =>
        (JsonParser parse path, Some(path))
      case Array("--raw", raw) =>
        (JsonParser parseRaw raw, None)
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

        originPathOpt match {
          case Some(originPath) =>
            new PlayerReloader(composer, originPath, Seconds(3), keyListener)
          case None =>
        }

        val out = new SoundAndFileOut("out.pcm")
        out.start()
        composer play out
        out.stop()
      case Failure(e) =>
        log.error("Got error from parsing JSON", e)
    }
  }
}
