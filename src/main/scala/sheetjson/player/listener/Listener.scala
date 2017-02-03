package sheetjson.player.listener

import sheetjson.util.Messagable

trait Listener extends Messagable {
  val name: Option[String] = None
}
