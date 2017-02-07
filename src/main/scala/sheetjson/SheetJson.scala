package sheetjson

import java.util.concurrent.ScheduledThreadPoolExecutor

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.management.gui.{GUI, Controller}
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
        val controller = new Controller()
        val gui = new GUI(controller)
        val keyListener = new KeyListener()
        gui.addKeyListener(keyListener)
        val composer = new Composer(controller)

        val playerLoader = new PlayerLoader(Seq(controller, keyListener, composer), originPath, keyListener)
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
