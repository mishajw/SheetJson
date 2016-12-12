package sheetjson.player
import sheetjson.management.KeyListener.KeyCode

trait ActivatablePlayer extends ListenerPlayer {

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
