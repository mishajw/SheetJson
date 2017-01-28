package sheetjson.player.listener

import sheetjson.player.Player

trait ListenerPlayer extends Player {
  val listeners: Seq[Listener]
}
