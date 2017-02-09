package sheetjson.management

import com.typesafe.scalalogging.Logger
import sheetjson.management.gui.Controller
import sheetjson.output.Out
import sheetjson.player.EndPlayable
import sheetjson.util.{Config, RootPlayerAssignable}

/**
  * Responsible for playing music from a Player object
  */
class Composer(controller: Controller) extends RootPlayerAssignable {

  private val log = Logger(getClass)

  /**
    * Play from a Player
    * @param out the output
    */
  def play(out: Out): Unit = {
    log.debug(s"Start taking from $rootPlayerOpt and putting into $out")

    while (rootPlayerOpt.forall(_.alive)) {
      rootPlayerOpt match {
        case Some(rootPlayer) =>
          out.play(rootPlayer.play)
        case None =>
      }

      Config.globalAbsoluteStep = Config.globalAbsoluteStep.incr

      controller.changed()
    }

    log.debug("Stop playing")

    out.play(new EndPlayable())
  }
}
