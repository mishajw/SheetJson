package sheetjson.management

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.management.gui.Model
import sheetjson.management.json.JsonParser
import sheetjson.util.Config
import sheetjson.util.Time.{Absolute, Bars, Seconds}

import scala.util.{Failure, Success}

class PlayerReloader(composer: Composer,
                     originPath: String,
                     reloadWaitTime: Seconds,
                     keyListener: KeyListener) extends Runnable {

  private val log = Logger(getClass)

  val executor = new ScheduledThreadPoolExecutor(1)
  val f = executor.scheduleAtFixedRate(
    this,
    (reloadWaitTime.toDouble * 1000).toLong,
    (reloadWaitTime.toDouble * 1000).toLong,
    TimeUnit.MILLISECONDS)

  override def run(): Unit = {
    JsonParser parse originPath match {
      case Success(newRootPlayer) =>
        log.info(s"Reloading players in $composer")

        // TODO: Make sure this is thread safe
        composer.rootPlayer = newRootPlayer
        newRootPlayer.propagateParents()
        Model.clearReadings()

        keyListener.clearListeners()
        keyListener.rootPlayer = newRootPlayer
        ListenerSetupOrganiser.setup(newRootPlayer, keyListener)
      case Failure(_) =>
    }
  }
}
