package sheetjson.player.listener

import sheetjson.util.Messagable.{Message, StringMessage}

trait IncrementableListener extends Listener {

  override def receive(message: Message): Unit = message match {
    case StringMessage("next") => next()
    case StringMessage("previous") => previous()
    case _ => super.receive(message)
  }

  def next()

  def previous()
}
