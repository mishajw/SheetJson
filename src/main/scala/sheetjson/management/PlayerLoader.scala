package sheetjson.management

import java.io.FileNotFoundException
import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import com.typesafe.scalalogging.Logger
import sheetjson.initialisation.json.{JsonParser, ListenerSetupOrganiser}
import sheetjson.input.KeyListener
import sheetjson.util.RootPlayerAssignable
import sheetjson.util.Time.{Bars, Seconds}

import scala.io.Source
import scala.util.{Failure, Success, Try}

class PlayerLoader(rootPlayerAssignables: Seq[RootPlayerAssignable],
                   originPath: String,
                   keyListener: KeyListener) {

  private val log = Logger(getClass)

  private var reloadFuture: Option[ScheduledFuture[_]] = None

  private var lastFileHash: Int = 0

  def load(printError: Boolean = true): Unit = {
    val result = for {
      jsonString <- getFile(originPath)
      if jsonString.hashCode != lastFileHash
      newRootPlayer <- JsonParser parseRaw jsonString
    } yield {
      lastFileHash = jsonString.hashCode

      newRootPlayer.propagateParents()

      // TODO: Make sure this is thread safe
      rootPlayerAssignables.foreach(_.setNewPlayer(newRootPlayer))

      ListenerSetupOrganiser.setup(newRootPlayer, keyListener)
    }

    result match {
      case Success(_) => log.info("Successful reload")
      case Failure(e) if printError => log.warn("Couldn't reload", e)
      case _ =>
    }
  }

  def getFile(path: String): Try[String] = {
    try {
      val f = Source.fromFile(path)
      val raw = f.mkString
      f.close()

      Success(raw)
    } catch {
      case e: FileNotFoundException =>
        Failure(e)
    }
  }

  def setupReload(reloadWaitTime: Bars) = {
    val executor = new ScheduledThreadPoolExecutor(1)
    reloadFuture = Some(executor.scheduleAtFixedRate(
      new Runnable() { override def run(): Unit = load(false) },
      (Seconds(reloadWaitTime).toDouble * 1000).toLong,
      (Seconds(reloadWaitTime).toDouble * 1000).toLong,
      TimeUnit.MILLISECONDS))
  }

}
