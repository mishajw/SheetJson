package sheetjson.management

import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.scalalogging.Logger
import sheetjson.input.KeyListener
import sheetjson.management.gui.Controller
import sheetjson.management.json.JsonParser
import sheetjson.util.RootPlayerAssignable
import sheetjson.util.Time.{Bars, Seconds}

import scala.util.{Failure, Success}

class PlayerLoader(rootPlayerAssignables: Seq[RootPlayerAssignable],
                   originPath: String,
                   keyListener: KeyListener) extends Runnable {

  private val log = Logger(getClass)

  private var reloadFuture: Option[ScheduledFuture[_]] = None

  override def run(): Unit = {
    JsonParser parse originPath match {
      case Success(newRootPlayer) =>
        newRootPlayer.propagateParents()

        // TODO: Make sure this is thread safe
        rootPlayerAssignables.foreach(_.setNewPlayer(newRootPlayer))

        ListenerSetupOrganiser.setup(newRootPlayer, keyListener)
      case Failure(_) =>
    }
  }

  def setupReload(reloadWaitTime: Bars) = {
    val executor = new ScheduledThreadPoolExecutor(1)
    reloadFuture = Some(executor.scheduleAtFixedRate(
      this,
      (Seconds(reloadWaitTime).toDouble * 1000).toLong,
      (Seconds(reloadWaitTime).toDouble * 1000).toLong,
      TimeUnit.MILLISECONDS))
  }

}
