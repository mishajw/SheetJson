package sheetjson.player.activatable

import sheetjson.management.KeyListener.KeyCode
import sheetjson.player.activatable.InteractivePlayer.InteractiveSpec
import sheetjson.player.activatable.SingleKeyInteractivePlayer.SingleKeyInteractiveSpec

trait SingleKeyInteractivePlayer extends InteractivePlayer[SingleKeyInteractiveSpec] {

  protected var _isActive = false

  def isActive = _isActive

  override def keyPressed(kc: KeyCode): Unit = {
    _isActive = true
    activate()
  }

  override def keyReleased(kc: KeyCode): Unit = {
    _isActive = false
    deactivate()
  }

  def activate()

  def deactivate()
}

object SingleKeyInteractivePlayer {
  case class SingleKeyInteractiveSpec(key: KeyCode)
    extends InteractiveSpec { override def keys = Seq(key) }
}
