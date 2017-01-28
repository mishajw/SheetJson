package sheetjson.player.listener

import scala.collection.mutable.ArrayBuffer

trait MultiActivatableListener extends Listener {

  val rActivate = """activate\(([0-9]+)\)""".r
  val rDeactivate = """deactivate\(([0-9]+)\)""".r

  override def receive(message: String): Unit = message match {
    case rActivate(i) => activate(i.toInt)
    case rDeactivate(i) => deactivate(i.toInt)
  }

  val size: Int

  private val _isActive: ArrayBuffer[Boolean] = ArrayBuffer.fill(size)(false)

  def isActive(i: Int) = _isActive(i)

  final def activate(i: Int) = {
    _isActive(i) = true
    _activate(i)
  }

  final def deactivate(i: Int) = {
    _isActive(i) = true
    _deactivate(i)
  }

  def _activate(i: Int)

  def _deactivate(i: Int)
}
