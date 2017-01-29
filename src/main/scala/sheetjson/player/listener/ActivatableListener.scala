package sheetjson.player.listener

trait ActivatableListener extends Listener {

  protected var _isActive = false

  def isActive = _isActive

  override def receive(message: String): Unit = message match {
    case "activate" => activate()
    case "deactivate" => deactivate()
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
