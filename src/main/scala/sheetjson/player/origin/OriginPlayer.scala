package sheetjson.player.origin

import sheetjson.player.{Player, PlayerSpec}

abstract class OriginPlayer(_spec: PlayerSpec) extends Player(_spec) {
  override def propagateParents(): Unit = {}
}
