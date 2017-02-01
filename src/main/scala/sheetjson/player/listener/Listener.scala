package sheetjson.player.listener

import sheetjson.util.Identifiable

trait Listener extends Identifiable {
  val name: Option[String] = None

  def receive(message: String): Unit
}
