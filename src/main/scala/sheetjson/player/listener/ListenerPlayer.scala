package sheetjson.player.listener

import sheetjson.player.Player
import sheetjson.util.Identifiable

trait ListenerPlayer extends Player {
  val listeners: Seq[Listener]

  override def identifiableChildren: Seq[Identifiable] = super.identifiableChildren ++ listeners
}
