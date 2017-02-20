package sheetjson.player.listener

import sheetjson.util.Messagable.{Message, StringMessage}

trait ActivatableListener extends Listener {

  protected var _isActive = false

  def isActive = _isActive

  // TODO: Bring activate in
  override def receive(message: Message): Unit = message match {
    case StringMessage("activate") => activate()
    case StringMessage("deactivate") => deactivate()
    case StringMessage("toggle") => if (isActive) deactivate() else activate()
    case _ => super.receive(message)
  }

  final def activate() = {
    _isActive = true
    _activate()
  }

  final def deactivate() = {
    _isActive = false
    _deactivate()
  }

  protected def _activate()

  protected def _deactivate()
}
