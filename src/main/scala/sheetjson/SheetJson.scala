package sheetjson

import java.util.concurrent.ScheduledThreadPoolExecutor

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.management.gui.{GUI, Model}
import sheetjson.management.json.JsonParser
import sheetjson.management.{Composer, ListenerSetupOrganiser, PlayerLoader}
import sheetjson.output.SoundAndFileOut
import sheetjson.player.Player
import sheetjson.util.Config
import sheetjson.util.Time.{Absolute, Bars, Seconds}

import scala.util.{Failure, Success}

object SheetJson {

  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {

    val originPathOpt = args match {
      case Array("--path", path) =>
        Some(path)
      case _ =>
        log.error("Usage: sheet-json --path <file path>")
        return
    }

    originPathOpt match {
      case Some(originPath) =>
        val model = new Model()
        val gui = new GUI(model)
        val keyListener = new KeyListener(gui)
        val composer = new Composer(model)

        val playerLoader = new PlayerLoader(Seq(model, keyListener, composer), originPath, keyListener)
        playerLoader.run()
        playerLoader.setupReload(Seconds(5))

        val out = new SoundAndFileOut("out.pcm")
        out.start()
        composer play out
        out.stop()
      case _ =>
    }
  }
}
