package sheetjson

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.management.gui.{Controller, GUI}
import sheetjson.management.{Composer, PlayerLoader}
import sheetjson.output.SoundAndFileOut
import sheetjson.util.Time.Bars

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
        playerLoader.setupReload(Bars(2))

        val out = new SoundAndFileOut("out.pcm")
        out.start()
        composer play out
        out.stop()
      case _ =>
    }
  }
}
