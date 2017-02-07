package sheetjson.util

import sheetjson.player.Player

trait RootPlayerAssignable {
  protected var rootPlayerOpt: Option[Player] = None

  // TODO: Find out why overriding setter doesn't compile
  def setNewPlayer(newRootPlayer: Player): Unit = {
    rootPlayerOpt = Some(newRootPlayer)
    newPlayerAssigned()
  }

  protected def newPlayerAssigned(): Unit = {}
}
