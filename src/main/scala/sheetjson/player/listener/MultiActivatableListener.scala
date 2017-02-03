package sheetjson.player.listener

import sheetjson.util.Messagable.{Message, StringMessage}

import scala.collection.mutable.ArrayBuffer

trait MultiActivatableListener extends Listener {

  val rActivate = """activate\(([0-9]+)\)""".r
  val rDeactivate = """deactivate\(([0-9]+)\)""".r

  override def receive(message: Message): Unit = message match {
    case StringMessage(rActivate(i)) => activate(i.toInt)
    case StringMessage(rDeactivate(i)) => deactivate(i.toInt)
    case _ => super.receive(message)
  }

  val size: Int

  private lazy val _isActive: ArrayBuffer[Boolean] = ArrayBuffer.fill(size){false}

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
